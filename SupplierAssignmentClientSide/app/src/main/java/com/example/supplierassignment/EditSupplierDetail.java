package com.example.supplierassignment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.supplierassignment.databinding.ActivityEditSupplierDetailBinding;

import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class EditSupplierDetail extends AppCompatActivity {

    public static final String EXTRA_SUPPLIER_ID = "SUPPLIER_ID";
    private static final String[] SUPPLIER_TYPES = {
            "Type 1: Only Contract",
            "Type 2: Only Stock",
            "Type 3: Both Contract & Stock"
    };

    private ActivityEditSupplierDetailBinding binding;
    private SupplierRepository repository;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityEditSupplierDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new SupplierRepository(this);
        setupTypeDropdown();
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getIntExtra(EXTRA_SUPPLIER_ID, -1);
        if (id == -1) {
            Toast.makeText(this, "Invalid Supplier ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSupplier();

        binding.btnUpdateSupplier.setOnClickListener(v -> saveSupplier());
        binding.btnDeleteSupplier.setOnClickListener(v -> deleteSupplier());
    }

    private void setupTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, SUPPLIER_TYPES);
        binding.actvSupplierType.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSupplier() {
        repository.deleteSupplier(id);
        Toast.makeText(this, "Supplier deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadSupplier() {
        Supplier supplier = repository.getSupplierById(id);
        if (supplier != null) {
            binding.etSupplierName.setText(supplier.getInfo());
            binding.etReservedDays.setText(supplier.getReservedDays());
            
            // Set the correct dropdown value based on integer type
            int typeIndex = supplier.getType() - 1;
            if (typeIndex >= 0 && typeIndex < SUPPLIER_TYPES.length) {
                binding.actvSupplierType.setText(SUPPLIER_TYPES[typeIndex], false);
            }
        }
    }

    private void saveSupplier() {
        String name = binding.etSupplierName.getText().toString().trim();
        String selectedTypeText = binding.actvSupplierType.getText().toString();
        String reservedStr = binding.etReservedDays.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etSupplierName.setError("Name is required");
            return;
        }

        int type = 0;
        for (int i = 0; i < SUPPLIER_TYPES.length; i++) {
            if (SUPPLIER_TYPES[i].equals(selectedTypeText)) {
                type = i + 1;
                break;
            }
        }

        if (type == 0) {
            Toast.makeText(this, "Please select a supplier type", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Supplier updatedSupplier = new Supplier(id, name, type, reservedStr);
            repository.updateSupplier(updatedSupplier);
            
            Toast.makeText(this, "Supplier updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}