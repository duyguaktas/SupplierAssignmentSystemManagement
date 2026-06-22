package com.example.supplierassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.supplierassignment.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SupplierViewModel supplierViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supplierViewModel = new ViewModelProvider(requireParentFragment()).get(SupplierViewModel.class);

        supplierViewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                supplierViewModel.clearToastMessage();
            }
        });

        // Set up Toolbar Menu for Reset Database
        binding.toolbar.inflateMenu(R.menu.menu_send_to_server);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_reset_db) {
                showResetDialog();
                return true;
            }
            return false;
        });

        NavController navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(binding.toolbar, navController);

        // Set up navigation to Add Supplier
        binding.addSupplier.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_addSupplier));

        // Set up navigation to Edit/Search Suppliers
        binding.btnSearch.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_editSupplier));

        // Set up navigation to Send to Server
        binding.btnSendToServer.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_sendToServerFragment));
    }

    private void showResetDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reset Database")
                .setMessage("This will delete all suppliers and assignments, and reset IDs to 1. Are you sure?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    supplierViewModel.resetDatabase();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
