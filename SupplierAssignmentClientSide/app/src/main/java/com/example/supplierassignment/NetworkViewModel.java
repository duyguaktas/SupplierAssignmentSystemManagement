package com.example.supplierassignment;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NetworkViewModel extends AndroidViewModel {

    private final SupplierRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _statusMessage = new MutableLiveData<>();
    public LiveData<String> statusMessage = _statusMessage;

    public NetworkViewModel(@NonNull Application application) {
        super(application);
        repository = new SupplierRepository(application);
    }

    public void sendDataToServer(String ip, int port) {
        _isLoading.setValue(true);
        disposables.add(repository.sendDataAndReceiveResponseSingle(ip, port)
                .flatMapCompletable(repository::handleServerResponseCompletable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(() -> {
                    _statusMessage.setValue("Data synced successfully!");
                }, throwable -> {
                    _statusMessage.setValue("Error: " + throwable.getMessage());
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}