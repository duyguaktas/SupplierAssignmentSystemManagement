package com.example.supplierassignment;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "Suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;
    
    @ColumnInfo(name = "info")
    @SerializedName("info")
    private String info;
    
    @ColumnInfo(name = "type")
    @SerializedName("type")
    private SupplierType type;
    
    @ColumnInfo(name = "reservedDays")
    private String reservedDays;

    @Ignore
    @SerializedName("reservedDays")
    private transient List<Integer> reservedDaysList;

    public Supplier(int id, String info, SupplierType type, String reservedDays) {
        setId(id);
        setInfo(info);
        setType(type);
        setReservedDays(reservedDays);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public SupplierType getType() {
        return type;
    }

    public void setType(SupplierType type) {
        this.type = type;
    }

    public String getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(String reservedDays) {
        this.reservedDaysList = rawToFormattedList(reservedDays);
        this.reservedDays = formatReservedDays(this.reservedDaysList);
    }

    public List<Integer> getReservedDaysList() {
        if (reservedDaysList == null) {
            reservedDaysList = rawToFormattedList(reservedDays);
        }
        return reservedDaysList;
    }

    public void setReservedDaysList(List<Integer> reservedDaysList) {
        this.reservedDaysList = reservedDaysList;
        this.reservedDays = formatReservedDays(reservedDaysList);
    }

    private List<Integer> rawToFormattedList(String rawInput) {
        List<Integer> dayList = new ArrayList<>();
        if (rawInput == null || rawInput.trim().isEmpty()) return dayList;

        String[] daysArray = rawInput.split(",");
        for (String day : daysArray) {
            try {
                int d = Integer.parseInt(day.trim());
                if (d >= 1 && d <= 31 && !dayList.contains(d)) {
                    dayList.add(d);
                }
            } catch (NumberFormatException ignored) {}
        }
        Collections.sort(dayList);
        return dayList;
    }

    public static String formatReservedDays(List<Integer> dayList){
        if (dayList == null || dayList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dayList.size(); i++) {
            sb.append(dayList.get(i));
            if (i < dayList.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return info + " (Type " + type + ")";
    }
}
