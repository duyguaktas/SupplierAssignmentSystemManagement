package com.example.supplierassignment;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkViewModel extends AndroidViewModel {

    private final SupplierRepository repository;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _statusMessage = new MutableLiveData<>();

    public NetworkViewModel(@NonNull Application application) {
        super(application);
        repository = new SupplierRepository(application);
    }

    public void sendDataToServer(String ip, int port) {
        _isLoading.setValue(true);
        new Thread(() -> {
            try {
                String response = repository.sendDataAndReceiveResponse(ip, port);
                repository.handleServerResponse(response);
                _statusMessage.postValue("Data synced successfully!");
            } catch (Exception e) {
                _statusMessage.postValue("Error: " + e.getMessage());
            } finally {
                _isLoading.postValue(false);
            }
        }).start();
    }
}