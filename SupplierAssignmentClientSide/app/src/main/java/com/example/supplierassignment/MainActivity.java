package com.example.supplierassignment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.supplierassignment.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize View Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up navigation to Add Supplier
        binding.addSupplier.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, AddSupplier.class)));
        
        // Set up navigation to Edit/Search Suppliers
        binding.btnSearch.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, EditSupplier.class)));

        // Set up navigation to Send to Server
        binding.btnSendToServer.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, SendToServerActivity.class)));
    }
}