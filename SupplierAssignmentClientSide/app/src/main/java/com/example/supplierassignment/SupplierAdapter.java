package com.example.supplierassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class SupplierAdapter extends ListAdapter<Supplier, SupplierAdapter.SupplierViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Supplier supplier);
    }

    public SupplierAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull Supplier oldItem, @NonNull Supplier newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Supplier oldItem, @NonNull Supplier newItem) {
                return oldItem.getInfo().equals(newItem.getInfo()) &&
                        oldItem.getType().equals(newItem.getType()) &&
                        oldItem.getReservedDays().equals(newItem.getReservedDays());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.supplier_list_item, parent, false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        Supplier supplier = getItem(position);
        holder.bind(supplier, listener);
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvType, tvReserved;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewSupplierName);
            tvType = itemView.findViewById(R.id.textViewSupplierType);
            tvReserved = itemView.findViewById(R.id.textViewReservedDays);
        }

        public void bind(Supplier supplier, OnItemClickListener listener) {
            tvName.setText(supplier.getInfo());

            tvType.setText(supplier.getType().getDescription());

            String days = supplier.getReservedDays();
            tvReserved.setText(days.isEmpty() ? "" : days);

            itemView.setOnClickListener(v -> listener.onItemClick(supplier));
        }
    }
}