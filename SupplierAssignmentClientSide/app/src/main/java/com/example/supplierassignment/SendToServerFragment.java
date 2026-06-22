package com.example.supplierassignment;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.supplierassignment.databinding.FragmentSendToServerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SendToServerFragment extends Fragment {

    private FragmentSendToServerBinding binding;
    
    private NetworkViewModel networkViewModel;
    private SupplierViewModel supplierViewModel;
    private SupplierAdapter adapter;
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSendToServerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       networkViewModel = new ViewModelProvider(requireParentFragment()).get(NetworkViewModel.class);
       supplierViewModel = new ViewModelProvider(requireParentFragment()).get(SupplierViewModel.class);
        adapter = new SupplierAdapter(supplier -> {
            Bundle bundle = new Bundle();
            bundle.putInt(EditSupplierDetailFragment.EXTRA_SUPPLIER_ID, supplier.getId());
            NavHostFragment.findNavController(this).navigate(R.id.action_sendToServerFragment_to_editSupplierDetail, bundle);
        });

        binding.rvSuppliers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSuppliers.setAdapter(adapter);

        supplierViewModel.getSuppliers().observe(getViewLifecycleOwner(), suppliers -> {
            adapter.submitList(suppliers);
        });

        supplierViewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                supplierViewModel.setSearchQuery(""); // Refresh list on reset/update
                supplierViewModel.clearToastMessage();
            }
        });

        networkViewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            binding.btnSendToServer.setEnabled(!loading);
            binding.btnSendToServer.setText(loading ? "Connecting..." : "Connect & Send");
        });

        supplierViewModel.setSearchQuery("");
        
        NavController navController = Navigation.findNavController(view);
        
        NavigationUI.setupWithNavController(binding.toolbar, navController);
        
        binding.toolbar.setOnMenuItemClickListener(item -> { if (item.getItemId() == R.id.action_reset_db) { showResetDialog(); return true; } return false; });

        binding.btnSendToServer.setOnClickListener(v -> showConnectionDialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showResetDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reset Database")
                .setMessage("This will delete all suppliers and assignments, and reset IDs to 1. Are you sure?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    supplierViewModel.resetDatabase();
                }).setNegativeButton("Cancel", null).show(); }

    private void showConnectionDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, 0);

        final TextInputLayout tilIP = new TextInputLayout(requireContext());
        tilIP.setHint("Server IP Address");
        final TextInputEditText etIP = new TextInputEditText(requireContext());
        etIP.setText("10.0.2.2"); // Default for emulator
        tilIP.addView(etIP);
        layout.addView(tilIP);

        final TextInputLayout tilPort = new TextInputLayout(requireContext());
        tilPort.setHint("Port Number");
        tilPort.setPadding(0, padding, 0, 0);
        final TextInputEditText etPort = new TextInputEditText(requireContext());
        etPort.setHint("e.g. 8080");
        etPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        tilPort.addView(etPort);
        layout.addView(tilPort);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Connect to Server")
                .setView(layout)
                .setPositiveButton("Connect", (dialog, which) -> {
                    String ip = etIP.getText().toString();
                    if (ip.isEmpty()) {
                        Toast.makeText(requireContext(), "IP address cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String portStr = etPort.getText().toString();
                    if(portStr.isEmpty()){
                        Toast.makeText(requireContext(), "Port cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int port = Integer.parseInt(portStr);
                        networkViewModel.sendDataToServer(ip, port);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid port number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }


}
