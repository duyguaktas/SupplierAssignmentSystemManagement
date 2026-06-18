package com.example.supplierassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "Suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "info")
    private String info;
    
    @ColumnInfo(name = "type")
    private int type;
    
    @ColumnInfo(name = "reservedDays")
    private String reservedDays;

    public Supplier(int id, String info, int type, String reservedDays) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if(type<=3 && type>= 1) {
            this.type = type;
        } else throw new IllegalArgumentException("Supplier type must be between 1 and 3");
    }

    public String getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(String reservedDays) {
        this.reservedDays = formatReservedDays(reservedDays);
    }

    public static String formatReservedDays(String rawInput){
        if (rawInput == null || rawInput.trim().isEmpty()) return "";

        String[] daysArray = rawInput.split(",");
        java.util.List<Integer> dayList = new java.util.ArrayList<>();

        for (String day : daysArray) {
            try {
                int d = Integer.parseInt(day.trim());
                if (d >= 1 && d <= 31 && !dayList.contains(d)) {
                    dayList.add(d);
                }
            } catch (NumberFormatException ignored) {}
        }

        java.util.Collections.sort(dayList);

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