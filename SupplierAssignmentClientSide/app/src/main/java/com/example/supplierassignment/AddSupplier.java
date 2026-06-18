package com.example.supplierassignment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.supplierassignment.databinding.ActivityAddSupplierBinding;

public class AddSupplier extends AppCompatActivity {

    private ActivityAddSupplierBinding binding;
    private SupplierRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityAddSupplierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        repository = new SupplierRepository(this);
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSaveSupplier.setOnClickListener(v -> addSupplier());
    }

    private void addSupplier() {
        String supplierName = binding.etSupplierName.getText().toString().trim();
        int selectedTypeId = binding.rgSupplierType.getCheckedRadioButtonId();

        if (supplierName.isEmpty()) {
            binding.etSupplierName.setError("Supplier name is required");
            return;
        }

        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select a supplier type", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = 1;
        if (selectedTypeId == R.id.rbType2) {
            type = 2;
        } else if (selectedTypeId == R.id.rbType3) {
            type = 3;
        }

        try {
            repository.addSupplier(supplierName, type);
            Toast.makeText(this, "Supplier added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to add supplier", Toast.LENGTH_SHORT).show();
        }
    }
}