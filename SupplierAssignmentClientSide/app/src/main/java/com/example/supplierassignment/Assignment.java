package com.example.supplierassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "SupplierAssignments")
public class Assignment {
    @PrimaryKey(autoGenerate = true)
    private int id;
     private String contractSupplier;
    private String stockSupplier;
    @SerializedName("dayOfTheMonth")
    private int dayOfTheMonth;

    private int month;

    public Assignment(String contractSupplier, String stockSupplier, int dayOfTheMonth, int month) {
        this.contractSupplier = contractSupplier;
        this.stockSupplier = stockSupplier;
        this.dayOfTheMonth = dayOfTheMonth;
        this.month = month;
    }

    // Add your Getters and Setters here (Required for Room)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContractSupplier() { return contractSupplier; }
    public void setContractSupplier(String contractSupplier) { this.contractSupplier = contractSupplier; }
    public String getStockSupplier() { return stockSupplier; }
    public void setStockSupplier(String stockSupplier) { this.stockSupplier = stockSupplier; }
    public int getDayOfTheMonth() { return dayOfTheMonth; }
    public void setDayOfTheMonth(int dayOfTheMonth) { this.dayOfTheMonth = dayOfTheMonth; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
}
