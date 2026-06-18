package com.example.supplierassignment;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SupplierDao {
    @Query("SELECT * FROM Suppliers")
    List<Supplier> getAllSuppliers();

    @Query("SELECT * FROM Suppliers WHERE info LIKE :query")
    List<Supplier> searchSuppliers(String query);

    @Query("SELECT * FROM Suppliers WHERE id = :id")
    Supplier getSupplierById(int id);

    @Insert
    void insertSupplier(Supplier supplier);

    @Update
    void updateSupplier(Supplier supplier);

    @Delete
    void deleteSupplier(Supplier supplier);

    @Query("DELETE FROM Suppliers WHERE id = :id")
    void deleteById(int id);

    @Insert
    void insertAssignment(Assignment assignment);

    @Query("SELECT * FROM SupplierAssignments")
    List<Assignment> getAllAssignments();

    @Query("DELETE FROM SupplierAssignments")
    void deleteAllAssignments();

    @Query("DELETE FROM Suppliers")
    void deleteAllSuppliers();

    @Query("DELETE FROM sqlite_sequence WHERE name='SupplierAssignments' OR name='Suppliers'")
    void resetSequences();
}