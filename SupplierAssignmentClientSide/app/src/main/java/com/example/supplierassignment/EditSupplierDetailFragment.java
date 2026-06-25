package com.example.supplierassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
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

import com.example.supplierassignment.databinding.FragmentEditSupplierDetailBinding;

import java.util.Objects;

public class EditSupplierDetailFragment extends Fragment {

    public static final String EXTRA_SUPPLIER_ID = "SUPPLIER_ID";
    private static final String[] SUPPLIER_TYPES = {
            "Type 1: Only Contract",
            "Type 2: Only Stock",
            "Type 3: Both Contract & Stock"
    };

    private FragmentEditSupplierDetailBinding binding;
    private SupplierViewModel supplierViewModel;
    private int id;
    private int selectedTypeIndex = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditSupplierDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EdgeToEdge.enable((ComponentActivity) requireContext());

        NavController navController = Navigation.findNavController(view);

        NavigationUI.setupWithNavController(binding.toolbar, navController);

        supplierViewModel = new ViewModelProvider(requireParentFragment()).get(SupplierViewModel.class);

        supplierViewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                supplierViewModel.clearToastMessage();
            }
        });

        supplierViewModel.navigationEvent.observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                navController.navigateUp();
                supplierViewModel.clearNavigationEvent();
            }
        });

        supplierViewModel.validationState.observe(getViewLifecycleOwner(), state -> {
            binding.tilSupplierName.setError(state.nameError);
            binding.tilReservedDays.setError(state.reservedDaysError);
            if (state.typeError != null) {
                Toast.makeText(requireContext(), state.typeError, Toast.LENGTH_SHORT).show();
            }
        });

        supplierViewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            binding.btnUpdateSupplier.setEnabled(!loading);
            binding.btnDeleteSupplier.setEnabled(!loading);
        });

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
        binding.actvSupplierType.setOnItemClickListener((parent, view, position, id) -> {
            selectedTypeIndex = position;
        });
    }

    private void deleteSupplier() {
        supplierViewModel.deleteSupplier(id);
    }

    private void loadSupplier() {
        supplierViewModel.getSupplierByIdLiveData(id).observe(getViewLifecycleOwner(), supplier -> {
            if (supplier != null) {
                binding.etSupplierName.setText(supplier.getInfo());
                binding.etReservedDays.setText(supplier.getReservedDays());

                // Set the correct dropdown value based on integer type
                int typeValue = supplier.getType().getValue();
                int typeIndex = typeValue - 1;
                if (typeIndex >= 0 && typeIndex < SUPPLIER_TYPES.length) {
                    binding.actvSupplierType.setText(SUPPLIER_TYPES[typeIndex], false);
                    selectedTypeIndex = typeIndex;
                }
            }
        });
    }

    private void saveSupplier() {
        String name = Objects.requireNonNull(binding.etSupplierName.getText()).toString().trim();
        String reservedStr = Objects.requireNonNull(binding.etReservedDays.getText()).toString().trim();

        supplierViewModel.validateAndSave(id, name, selectedTypeIndex, reservedStr, true);
    }
}