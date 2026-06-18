package com.example.supplierassignment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.supplierassignment.databinding.ActivityEditSupplierDetailBinding;

public class EditSupplierDetail extends AppCompatActivity {

    public static final String EXTRA_SUPPLIER_ID = "SUPPLIER_ID";

    private ActivityEditSupplierDetailBinding binding;
    private SupplierRepository repository;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityEditSupplierDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new SupplierRepository(this);

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

    private void deleteSupplier() {
        repository.deleteSupplier(id);
        Toast.makeText(this, "Supplier deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadSupplier() {
        Supplier supplier = repository.getSupplierById(id);
        if (supplier != null) {
            binding.etSupplierName.setText(supplier.getInfo());
            binding.etSupplierType.setText(String.valueOf(supplier.getType()));
            binding.etReservedDays.setText(supplier.getReservedDays());
        }
    }

    private void saveSupplier() {
        String name = binding.etSupplierName.getText().toString().trim();
        String typeStr = binding.etSupplierType.getText().toString().trim();
        String reservedStr = binding.etReservedDays.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etSupplierName.setError("Name is required");
            return;
        }

        if (typeStr.isEmpty()) {
            binding.etSupplierType.setError("Type is required");
            return;
        }

        try {
            int type = Integer.parseInt(typeStr);
            
            // The Supplier constructor/setter handles the formatting of reservedStr
            Supplier updatedSupplier = new Supplier(id, name, type, reservedStr);
            repository.updateSupplier(updatedSupplier);
            
            Toast.makeText(this, "Supplier updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Type must be a number (1, 2, or 3)", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}