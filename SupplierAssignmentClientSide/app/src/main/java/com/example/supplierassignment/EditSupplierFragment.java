package com.example.supplierassignment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.supplierassignment.databinding.FragmentEditSupplierBinding;

import java.util.ArrayList;
import java.util.List;

public class EditSupplierFragment extends Fragment {

    private FragmentEditSupplierBinding binding;
    private SupplierRepository repository;
    private CustomAdapter adapter;

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

        repository = new SupplierRepository(requireContext());
        setupListView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupListView() {
        adapter = new CustomAdapter(requireContext(), new ArrayList<>());
        binding.lvSuppliers.setAdapter(adapter);

        binding.lvSuppliers.setOnItemClickListener((parent, view, position, id) -> {
            Supplier selectedSupplier = adapter.getItem(position);
            if (selectedSupplier != null) {
                Bundle bundle = new Bundle();
                bundle.putInt(EditSupplierDetailFragment.EXTRA_SUPPLIER_ID, selectedSupplier.getId());

                Navigation.findNavController(view).navigate(
                        R.id.action_editSupplier_to_editSupplierDetail,
                        bundle
                );
            }
        });

        binding.btnSearch.setOnClickListener(v -> {
            binding.lvSuppliers.setVisibility(View.VISIBLE);
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

        updateList("");
    }

    private void updateList(String query) {
        List<Supplier> results = repository.getAllSuppliers(query);
        adapter.clear();
        adapter.addAll(results);
        adapter.notifyDataSetChanged();
    }

}

class CustomAdapter extends ArrayAdapter<Supplier>{

    public CustomAdapter(@NonNull Context context, List<Supplier> suppliers) {
        super(context, 0, suppliers);
    }
    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent){
        Supplier supplier = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.supplier_list_item, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.textViewSupplierName);
        TextView tvType = convertView.findViewById(R.id.textViewSupplierType);
        TextView tvReserved = convertView.findViewById(R.id.textViewReservedDays);
    
        if (supplier != null) {
            tvName.setText(supplier.getInfo());
            tvType.setText(String.valueOf(supplier.getType()));
            tvReserved.setText(supplier.getReservedDays());
        }

        return convertView;
    }
}