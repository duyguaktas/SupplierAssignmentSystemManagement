package com.example.supplierassignment;

import android.content.Context;
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
}
