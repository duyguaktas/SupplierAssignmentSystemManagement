package com.example.supplierassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "SupplierAssignments")
public class Assignment {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @Expose
    @SerializedName("contractSupplier")
    private String contractSupplier;

    @Expose
    @SerializedName("stockSupplier")
    private String stockSupplier;

    @Expose
    @SerializedName("dayOfTheMonth")
    private int dayOfMonth;

    private int month;

    public Assignment(String contractSupplier, String stockSupplier, int dayOfMonth, int month) {
        this.contractSupplier = contractSupplier;
        this.stockSupplier = stockSupplier;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
    }

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
