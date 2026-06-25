package com.example.supplierassignment;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SupplierViewModel extends AndroidViewModel {

    private final SupplierRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Supplier>> suppliers;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Boolean> _navigationEvent = new MutableLiveData<>(false);
    public LiveData<Boolean> navigationEvent = _navigationEvent;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public SupplierViewModel(@NonNull Application application) {
        super(application);
        repository = new SupplierRepository(application);

        suppliers = Transformations.switchMap(searchQuery, query -> 
                LiveDataReactiveStreams.fromPublisher(repository.getAllSuppliersFlowable(query))
        );
    }

    public void clearNavigationEvent() {
        _navigationEvent.setValue(false);
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
        disposables.add(repository.updateSupplier(supplier)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> _isLoading.setValue(true))
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(() -> {
                    _toastMessage.setValue("Supplier updated successfully");
                    _navigationEvent.setValue(true);
                }, throwable -> {
                    _toastMessage.setValue("Update failed: " + throwable.getMessage());
                }));
    }

    public void deleteSupplier(int id) {
        disposables.add(repository.deleteSupplier(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> _isLoading.setValue(true))
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(() -> {
                    _toastMessage.setValue("Supplier deleted successfully");
                    _navigationEvent.setValue(true);
                }, throwable -> {
                    _toastMessage.setValue("Delete failed: " + throwable.getMessage());
                }));
    }

    public void addSupplier(String name, int type){
        disposables.add(repository.addSupplier(name, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> _isLoading.setValue(true))
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(() -> {
                    _toastMessage.setValue("Supplier added successfully");
                    _navigationEvent.setValue(true);
                }, throwable -> {
                    _toastMessage.setValue("Failed to add supplier: " + throwable.getMessage());
                }));
    }

    public Supplier getSupplierById(int id) {
        return repository.getSupplierById(id);
    }

    public LiveData<Supplier> getSupplierByIdLiveData(int id) {
        return LiveDataReactiveStreams.fromPublisher(repository.getSupplierByIdFlowable(id));
    }

    public List<Supplier> getAllSuppliers(String query) {
        return repository.getAllSuppliers(query);
    }

    public void resetDatabase() {
        disposables.add(repository.resetDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> _isLoading.setValue(true))
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(() -> {
                    _toastMessage.setValue("Database reset successfully");
                }, throwable -> {
                    _toastMessage.setValue("Reset failed: " + throwable.getMessage());
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}