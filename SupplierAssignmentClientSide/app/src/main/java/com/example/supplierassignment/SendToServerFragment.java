package com.example.supplierassignment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Objects;

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
        tilIP.setPlaceholderText("e.g 10.0.2.2"); // Default for emulator
        final TextInputEditText etIP = new TextInputEditText(requireContext());
        tilIP.addView(etIP);
        layout.addView(tilIP);

        final TextInputLayout tilPort = new TextInputLayout(requireContext());
        tilPort.setHint("Port Number");
        tilPort.setPlaceholderText("e.g. 8080");
        tilPort.setPadding(0, padding, 0, 0);
        final TextInputEditText etPort = new TextInputEditText(requireContext());
        etPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        tilPort.addView(etPort);
        layout.addView(tilPort);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Connect to Server")
                .setView(layout)
                .setPositiveButton("Connect", (d, which) -> {
                    String ip = Objects.requireNonNull(etIP.getText()).toString();
                    int port = Integer.parseInt(Objects.requireNonNull(etPort.getText()).toString());
                    networkViewModel.sendDataToServer(ip, port);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        Button connectButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged (CharSequence s, int before, int start, int count) {
                validateInputs(tilIP, tilPort, connectButton);
            }
            @Override public void afterTextChanged (Editable s) {}
        };

        etIP.addTextChangedListener(watcher);
        etPort.addTextChangedListener(watcher);

        // Initial validation check
        validateInputs(tilIP, tilPort, connectButton);
    }

    private void validateInputs(TextInputLayout tilIP, TextInputLayout tilPort, Button connectButton) {
        String ip = Objects.requireNonNull(tilIP.getEditText()).getText().toString();
        String portStr = Objects.requireNonNull(tilPort.getEditText()).getText().toString();
        String ipPattern = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";

        boolean isIPValid = ip.matches(ipPattern);
        boolean isPortValid = false;

        try {
            if (!portStr.isEmpty()) {
                int port = Integer.parseInt(portStr);
                isPortValid = (port >= 1) && (port <= 65535);
            }
        } catch (NumberFormatException ignored) {}

        // enable button only if both are valid
        connectButton.setEnabled(isIPValid && isPortValid);

        // show live error hints
        if (!ip.isEmpty() && !isIPValid) {
            tilIP.setError("Invalid IP format");
        } else {
            tilIP.setError(null);
        }

        if (!portStr.isEmpty() && !isPortValid) {
            tilPort.setError("Invalid port (1-65535)");
        } else {
            tilPort.setError(null);
        }
    }


}
