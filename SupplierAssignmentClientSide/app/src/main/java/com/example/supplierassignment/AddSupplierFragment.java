package com.example.supplierassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.supplierassignment.databinding.FragmentAddSupplierBinding;

public class AddSupplierFragment extends Fragment {

    private FragmentAddSupplierBinding binding;
    private SupplierViewModel supplierViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddSupplierBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supplierViewModel = new ViewModelProvider(requireParentFragment()).get(SupplierViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavController navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(binding.toolbar, navController);

        binding.btnSaveSupplier.setOnClickListener(v -> addSupplier());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addSupplier() {
        String supplierName = binding.etSupplierName.getText().toString().trim();
        int selectedTypeId = binding.rgSupplierType.getCheckedRadioButtonId();

        if (supplierName.isEmpty()) {
            binding.etSupplierName.setError("Supplier name is required");
            return;
        }

        if (selectedTypeId == -1) {
            Toast.makeText(requireContext(), "Please select a supplier type", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = 1;
        if (selectedTypeId == R.id.rbType2) {
            type = 2;
        } else if (selectedTypeId == R.id.rbType3) {
            type = 3;
        }

        try {
            supplierViewModel.addSupplier(supplierName, type);
            Toast.makeText(requireContext(), "Supplier added successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to add supplier", Toast.LENGTH_SHORT).show();
        }
    }
}