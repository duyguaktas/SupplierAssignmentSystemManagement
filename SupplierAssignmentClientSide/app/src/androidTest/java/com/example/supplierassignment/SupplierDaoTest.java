package com.example.supplierassignment;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class SupplierDaoTest {
    private AppDatabase db;
    private SupplierDao supplierDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        supplierDao = db.supplierDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetSupplier() throws Exception {
        Supplier supplier = new Supplier(1, "Test Supplier", 1, "1,2,3");
        supplierDao.insertSupplier(supplier);
        Supplier retrieved = supplierDao.getSupplierById(1);
        assertEquals(retrieved.getInfo(), (supplier.getInfo()));
    }

    @Test
    public void deleteSupplier() throws Exception{
        Supplier supplier = new Supplier(1, "Delete me", 1, "");
        supplierDao.insertSupplier(supplier);
        supplierDao.deleteById(supplier.getId());
        Supplier retrieved = supplierDao.getSupplierById(1);
        assertNull(retrieved);
    }

    @Test
    public void updateSupplier() throws Exception {
        Supplier supplier = new Supplier(1, "Original Name", 1, "A");
        supplierDao.insertSupplier(supplier);
        supplier.setInfo("Updated Name");
        supplierDao.updateSupplier(supplier);
        Supplier retrieved = supplierDao.getSupplierById(1);
        assertEquals("Updated Name", retrieved.getInfo());
    }

    @Test
    public void getAllSuppliers() throws Exception {
        Supplier supplier1 = new Supplier(1, "Supplier 1", 1, "A");
        Supplier supplier2 = new Supplier(2, "Supplier 2", 2, "B");
        supplierDao.insertSupplier(supplier1);
        supplierDao.insertSupplier(supplier2);
        List<Supplier> allSuppliers = supplierDao.getAllSuppliers();
        assertEquals(2, allSuppliers.size());
        assertEquals("Supplier 1", allSuppliers.get(0).getInfo());
    }

    @Test
    public void deleteAllSuppliers() throws Exception {
        Supplier supplier1 = new Supplier(1, "Supplier 1", 1, "A");
        Supplier supplier2 = new Supplier(2, "Supplier 2", 2, "B");
        supplierDao.insertSupplier(supplier1);
        supplierDao.insertSupplier(supplier2);
        supplierDao.deleteAllSuppliers();
        List<Supplier> allSuppliers = supplierDao.getAllSuppliers();
        assertEquals(0, allSuppliers.size());
    }

    @Test
    public void searchSuppliers() {
        supplierDao.insertSupplier(new Supplier(1, "Apple", 1, ""));
        supplierDao.insertSupplier(new Supplier(2, "Banana", 1, ""));

        List<Supplier> results = supplierDao.searchSuppliers("%App%");
        assertEquals(1, results.size());
        assertEquals("Apple", results.get(0).getInfo());
    }

    @Test
    public void searchSuppliers_noMatch() {
        supplierDao.insertSupplier(new Supplier(1, "Apple", 1, ""));

        List<Supplier> results = supplierDao.searchSuppliers("%Orange%");
        assertEquals(0, results.size());
    }

    @Test
    public void searchSuppliers_multipleMatches() {
        supplierDao.insertSupplier(new Supplier(1, "Apple Inc", 1, ""));
        supplierDao.insertSupplier(new Supplier(2, "Apple Corp", 1, ""));
        supplierDao.insertSupplier(new Supplier(3, "Banana", 1, ""));

        List<Supplier> results = supplierDao.searchSuppliers("%Apple%");
        assertEquals(2, results.size());
    }
}
