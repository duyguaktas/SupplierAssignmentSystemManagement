package com.example.supplierassignment;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class SupplierRepository {
    private final SupplierDao supplierDao;

    public SupplierRepository(Context context){
        AppDatabase db = AppDatabase.getInstance(context);
        this.supplierDao = db.supplierDao();
    }

    public List<Supplier> getAllSuppliers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return supplierDao.getAllSuppliers();
        } else {
            return supplierDao.searchSuppliers("%" + query.trim() + "%");
        }
    }

    public Single<List<Supplier>> getAllSuppliersSingle() {
        return supplierDao.getAllSuppliersSingle();
    }
    public Flowable<List<Supplier>> getAllSuppliersFlowable(String query){
        if (query == null || query.trim().isEmpty()) {
            return supplierDao.getAllSuppliersFlowable();
        } else {
            return supplierDao.searchSuppliersFlowable("%" + query.trim() + "%");
        }
    }

    public Supplier getSupplierById(int id) {
        return supplierDao.getSupplierById(id);
    }

    public Flowable<Supplier> getSupplierByIdFlowable(int id) {
        return supplierDao.getSupplierByIdFlowable(id);
    }

    public Completable updateSupplier(Supplier supplier) {
        return supplierDao.updateSupplier(supplier);
    }

    public Completable deleteSupplier(int id) {
        return supplierDao.deleteById(id);
    }

    public Completable addSupplier(String name, int typeValue) {
        Supplier supplier = new Supplier(0, name, SupplierType.fromInt(typeValue), "");
        return supplierDao.insertSupplier(supplier);
    }

    public Completable addAssignment(Assignment assignment) {
        return supplierDao.insertAssignment(assignment);
    }

    public Completable clearAssignments(){
        return supplierDao.deleteAllAssignments();
    }

    public Completable resetDatabase() {
        return supplierDao.deleteAllAssignments()
                .andThen(supplierDao.deleteAllSuppliers())
                .andThen(Completable.fromAction(supplierDao::resetSequences));
    }

    public List<Assignment> getAllAssignments(){
        return supplierDao.getAllAssignments();
    }
    
    public Single<String> sendDataAndReceiveResponseSingle(String ip, int port) {
        return Single.fromCallable(() -> {
            List<Supplier> suppliers = supplierDao.getAllSuppliers();

            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.util.Map<String, List<Supplier>> wrapper = new HashMap<>();
            wrapper.put("suppliers" , suppliers);
            String jsonRequest = gson.toJson(wrapper);

            try (Socket socket = new Socket(ip, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                socket.setSoTimeout(5000);
                out.println(jsonRequest);
                String response = in.readLine();
                if (response == null) throw new Exception("Server closed connection");
                return response;

            } catch (Exception e) {
                // stack trace
                throw new Exception("Error communicating with server: " + e.getMessage(), e);
            }
        });
    }

    public Completable handleServerResponseCompletable(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) return Completable.complete();

        return Single.fromCallable(() -> {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken<java.util.Map<String, List<Assignment>>>() {}.getType();
            java.util.Map<String, List<Assignment>> data = gson.fromJson(jsonResponse, mapType);
            return data.get("assignments");
        }).flatMapCompletable(assignments -> {
            if (assignments == null || assignments.isEmpty()) return Completable.complete();

            return supplierDao.deleteAllAssignments()
                    .andThen(io.reactivex.rxjava3.core.Observable.fromIterable(assignments)
                            .flatMapCompletable(supplierDao::insertAssignment))
                    .andThen(supplierDao.getAllSuppliersSingle()
                            .flatMapCompletable(allSuppliers -> {
                                java.util.Map<String, List<Integer>> supplierAssignedDays = new java.util.HashMap<>();
                                for (Assignment a : assignments) {
                                    String contract = a.getContractSupplier();
                                    if (contract != null) {
                                        supplierAssignedDays.computeIfAbsent(contract, k -> new java.util.ArrayList<>()).add(a.getDayOfTheMonth());
                                    }
                                    String stock = a.getStockSupplier();
                                    if (stock != null && !stock.equals(contract)) {
                                        supplierAssignedDays.computeIfAbsent(stock, k -> new java.util.ArrayList<>()).add(a.getDayOfTheMonth());
                                    }
                                }

                                java.util.List<Completable> updates = new java.util.ArrayList<>();
                                for (Supplier s : allSuppliers) {
                                    List<Integer> assignedDays = supplierAssignedDays.get(s.getInfo());
                                    if (assignedDays != null) {
                                        s.setReservedDaysList(assignedDays);
                                        updates.add(supplierDao.updateSupplier(s));
                                    }
                                }
                                return updates.isEmpty() ? Completable.complete() : Completable.concat(updates);
                            }));
        });
    }
}
