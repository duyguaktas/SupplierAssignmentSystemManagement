package com.example.supplierassignment;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

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
    public LiveData<List<Supplier>> getAllSuppliersLiveData(String query){
        if (query == null || query.trim().isEmpty()) {
            return supplierDao.getAllSuppliersLiveData();
        } else {
            return supplierDao.searchSuppliersLiveData("%" + query.trim() + "%");
        }
    }

    public Supplier getSupplierById(int id) {
        return supplierDao.getSupplierById(id);
    }

    public void updateSupplier(Supplier supplier) {
        supplierDao.updateSupplier(supplier);
    }

    public void deleteSupplier(int id) {
        supplierDao.deleteById(id);
    }

    public void addSupplier(String name, int type) {
        Supplier supplier = new Supplier(0, name, type, "");
        supplierDao.insertSupplier(supplier);
    }

    public void addAssignment(Assignment assignment) {
        supplierDao.insertAssignment(assignment);
    }

    public void clearAssignments(){
        supplierDao.deleteAllAssignments();
    }

    public void resetDatabase() {
        supplierDao.deleteAllAssignments();
        supplierDao.deleteAllSuppliers();
        supplierDao.resetSequences();
    }

    public List<Assignment> getAllAssignments(){
        return supplierDao.getAllAssignments();
    }
    
    public String sendDataAndReceiveResponse(String ip, int port) throws Exception {
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
            return in.readLine();
            
        } catch (Exception e) {
            throw new Exception("Error communicating with server: " + e.getMessage());
        }
    }
    
    public void handleServerResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) return;

        com.google.gson.Gson gson = new com.google.gson.Gson();
        java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken<java.util.Map<String, List<Assignment>>>() {}.getType();
        java.util.Map<String, List<Assignment>> data = gson.fromJson(jsonResponse, mapType);
        List<Assignment> assignments = data.get("assignments");

        if (assignments != null && !assignments.isEmpty()) {
            supplierDao.deleteAllAssignments();
            java.util.Map<String, List<Integer>> supplierAssignedDays = new java.util.HashMap<>();

            for (Assignment a : assignments) {
                supplierDao.insertAssignment(a);
                String contract = a.getContractSupplier();
                if (contract != null) {
                    supplierAssignedDays.computeIfAbsent(contract, k -> new java.util.ArrayList<>()).add(a.getDayOfTheMonth());
                }
                String stock = a.getStockSupplier();
                if (stock != null && !stock.equals(contract)) {
                    supplierAssignedDays.computeIfAbsent(stock, k -> new java.util.ArrayList<>()).add(a.getDayOfTheMonth());
                }
            }
            
            List<Supplier> allSuppliers = supplierDao.getAllSuppliers();
            for (Supplier s : allSuppliers) {
                List<Integer> assignedDays = supplierAssignedDays.get(s.getInfo());
                if (assignedDays != null) {
                    s.setReservedDaysList(assignedDays);
                    supplierDao.updateSupplier(s);
                }
            }
        }
    }
}
