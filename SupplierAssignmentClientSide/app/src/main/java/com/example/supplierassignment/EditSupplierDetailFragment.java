package com.example.supplierassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.supplierassignment.databinding.FragmentEditSupplierDetailBinding;

public class EditSupplierDetailFragment extends Fragment {

    public static final String EXTRA_SUPPLIER_ID = "SUPPLIER_ID";
    private static final String[] SUPPLIER_TYPES = {
            "Type 1: Only Contract",
            "Type 2: Only Stock",
            "Type 3: Both Contract & Stock"
    };

    private FragmentEditSupplierDetailBinding binding;
    private SupplierRepository repository;
    private int id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditSupplierDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EdgeToEdge.enable((ComponentActivity) requireContext());

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        repository = new SupplierRepository(requireContext());
        setupTypeDropdown();
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
             Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
             v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
             return WindowInsetsCompat.CONSUMED;
        });

        if (getArguments() != null) {
            id = getArguments().getInt(EXTRA_SUPPLIER_ID, -1);
        } else {
            id = -1;
        }

        if (id == -1) {
            Toast.makeText(requireContext(), "Invalid Supplier ID", Toast.LENGTH_SHORT).show();
            return;
        }

        loadSupplier();

        binding.btnUpdateSupplier.setOnClickListener(v -> saveSupplier());
        binding.btnDeleteSupplier.setOnClickListener(v -> deleteSupplier());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, SUPPLIER_TYPES);
        binding.actvSupplierType.setAdapter(adapter);
    }

    private void deleteSupplier() {
        repository.deleteSupplier(id);
        Toast.makeText(requireContext(), "Supplier deleted", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigateUp();
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
            Toast.makeText(requireContext(), "Please select a supplier type", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Supplier updatedSupplier = new Supplier(id, name, type, reservedStr);
            repository.updateSupplier(updatedSupplier);
            
            Toast.makeText(requireContext(), "Supplier updated successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}