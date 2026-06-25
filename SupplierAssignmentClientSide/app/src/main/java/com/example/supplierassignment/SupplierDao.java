package com.example.supplierassignment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SupplierDao {
    @Query("SELECT * FROM Suppliers")
    List<Supplier> getAllSuppliers();

    @Query("SELECT * FROM Suppliers")
    Single<List<Supplier>> getAllSuppliersSingle();

    @Query("SELECT * FROM Suppliers WHERE info LIKE :query")
    List<Supplier> searchSuppliers(String query);

    @Query("SELECT * FROM Suppliers WHERE id = :id")
    Supplier getSupplierById(int id);

    @Insert
    Completable insertSupplier(Supplier supplier);

    @Update
    Completable updateSupplier(Supplier supplier);

    @Query("DELETE FROM Suppliers WHERE id = :id")
    Completable deleteById(int id);

    @Insert
    Completable insertAssignment(Assignment assignment);

    @Query("SELECT * FROM SupplierAssignments")
    List<Assignment> getAllAssignments();

    @Query("SELECT * FROM SupplierAssignments")
    Single<List<Assignment>> getAllAssignmentsSingle();

    @Query("DELETE FROM SupplierAssignments")
    Completable deleteAllAssignments();

    @Query("DELETE FROM Suppliers")
    Completable deleteAllSuppliers();

    @Query("DELETE FROM sqlite_sequence WHERE name='SupplierAssignments' OR name='Suppliers'")
    void resetSequences(); // SQLite sequence reset might be tricky with Completable if not returned, but usually it works if it returns void/Completable.

    @Query("SELECT * FROM Suppliers")
    Flowable<List<Supplier>> getAllSuppliersFlowable();
    @Query("SELECT * FROM Suppliers WHERE info LIKE :query")
    Flowable<List<Supplier>> searchSuppliersFlowable(String query);
    @Query("SELECT * FROM Suppliers WHERE id = :id")
    Flowable<Supplier> getSupplierByIdFlowable(int id);
}