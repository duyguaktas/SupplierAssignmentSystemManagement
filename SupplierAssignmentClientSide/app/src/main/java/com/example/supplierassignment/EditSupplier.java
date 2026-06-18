package com.example.supplierassignment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.supplierassignment.databinding.ActivityEditSupplierBinding;

import java.util.ArrayList;
import java.util.List;

import android.view.MenuItem;

public class EditSupplier extends AppCompatActivity {

    private ActivityEditSupplierBinding binding;
    private SupplierRepository repository;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityEditSupplierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new SupplierRepository(this);
        setupListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListView() {
        adapter = new CustomAdapter(this, new ArrayList<>());
        binding.lvSuppliers.setAdapter(adapter);

        binding.lvSuppliers.setOnItemClickListener((parent, view, position, id) -> {
            Supplier selectedSupplier = adapter.getItem(position);
            if (selectedSupplier != null) {
                Intent intent = new Intent(EditSupplier.this, EditSupplierDetail.class);
                intent.putExtra(EditSupplierDetail.EXTRA_SUPPLIER_ID, selectedSupplier.getId());
                startActivity(intent);
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