package com.example.supplierassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SupplierAssignments")
public class Assignment {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String contractSupplier;
    private String stockSupplier;
    private int dayOfMonth;
    private int month;

    // Constructor
    public Assignment(String contractSupplier, String stockSupplier, int dayOfMonth, int month) {
        setContractSupplier(contractSupplier);
        setStockSupplier(stockSupplier);
        setDayOfMonth(dayOfMonth);
        setMonth(month);
    }

    // Add your Getters and Setters here (Required for Room)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContractSupplier() { return contractSupplier; }
    public void setContractSupplier(String contractSupplier) { this.contractSupplier = contractSupplier; }
    public String getStockSupplier() { return stockSupplier; }
    public void setStockSupplier(String stockSupplier) { this.stockSupplier = stockSupplier; }
    public int getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
}