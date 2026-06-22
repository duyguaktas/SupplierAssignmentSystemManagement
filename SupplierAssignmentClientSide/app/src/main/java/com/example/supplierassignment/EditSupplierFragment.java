package com.example.supplierassignment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.supplierassignment.databinding.FragmentEditSupplierBinding;

public class EditSupplierFragment extends Fragment {

    private FragmentEditSupplierBinding binding;
    private SupplierViewModel supplierViewModel;
    private SupplierAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditSupplierBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
        setupRecycleView();

        supplierViewModel = new ViewModelProvider(requireParentFragment()).get(SupplierViewModel.class);
        supplierViewModel.setSearchQuery("");
        supplierViewModel.getSuppliers().observe(getViewLifecycleOwner(), suppliers -> {
            adapter.submitList(suppliers);
        });
        String savedQuery = supplierViewModel.getSearchQueryValue();
        if (savedQuery != null && !savedQuery.isEmpty()) {
            binding.etSearchBar.setText(savedQuery);
            binding.rvSuppliers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecycleView() {
        adapter = new SupplierAdapter(supplier -> {
            Bundle bundle = new Bundle();
            bundle.putInt(EditSupplierDetailFragment.EXTRA_SUPPLIER_ID, supplier.getId());
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_editSupplier_to_editSupplierDetail, bundle
            );

        });
        binding.rvSuppliers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.rvSuppliers.setAdapter(adapter);

        binding.btnSearch.setOnClickListener(v -> {
            binding.rvSuppliers.setVisibility(View.VISIBLE);
            String query = binding.etSearchBar.getText().toString();
            updateList(query);
        });

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateList(String query) {
        supplierViewModel.setSearchQuery(query);
    }

}