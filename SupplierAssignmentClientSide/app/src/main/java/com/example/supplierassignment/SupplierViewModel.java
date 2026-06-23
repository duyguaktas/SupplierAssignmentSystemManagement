package com.example.supplierassignment;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import java.util.List;

public class SupplierViewModel extends AndroidViewModel {

    private final SupplierRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Supplier>> suppliers;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    public SupplierViewModel(@NonNull Application application) {
        super(application);
        repository = new SupplierRepository(application);

        suppliers = Transformations.switchMap(searchQuery, repository::getAllSuppliersLiveData
        );
    }

    public void clearToastMessage() {
        _toastMessage.setValue(null);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Supplier>> getSuppliers() {
        return suppliers;
    }

    public String getSearchQueryValue() {
        return searchQuery.getValue();
    }

    public void updateSupplier(Supplier supplier){
        new Thread(() -> {
            repository.updateSupplier(supplier);
        }).start();
    }

    public void deleteSupplier(int id) {
        new Thread(() -> {
            repository.deleteSupplier(id);
        }).start();
    }

    public void addSupplier(String name, int type){
        new Thread(() -> {
            repository.addSupplier(name, type);
        }).start();
    }

    public Supplier getSupplierById(int id) {
        return repository.getSupplierById(id);
    }

    public List<Supplier> getAllSuppliers(String query) {
        return repository.getAllSuppliers(query);
    }

    public void resetDatabase() {
        new Thread(() -> {
            repository.resetDatabase();
            _toastMessage.postValue("Database reset successfully");
        }).start();
    }
}