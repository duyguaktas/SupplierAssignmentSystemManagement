package com.example.supplierassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "Suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    @Expose
    @SerializedName("id")
    private int id;
    
    @ColumnInfo(name = "info")
    @Expose
    @SerializedName("info")
    private String info;
    
    @ColumnInfo(name = "type")
    @Expose
    @SerializedName("type")
    private int type;
    
    @ColumnInfo(name = "reservedDays")
    private String reservedDays;

    @Ignore
    @Expose
    @SerializedName("reservedDays")
    private List<Integer> reservedDaysList;

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
        this.reservedDays = formatReservedDays(rawToFormattedList(reservedDays));
        this.reservedDaysList = rawToFormattedList(this.reservedDays);
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
