package com.example.supplierassignment;

import androidx.room.TypeConverter;

public class SupplierTypeConverter {
    @TypeConverter
    public static int fromSupplierType(SupplierType type) {
        return type == null ? SupplierType.CONTRACT_ONLY.getValue() : type.getValue();
    }

    @TypeConverter
    public static SupplierType toSupplierType(int value) {
        return SupplierType.fromInt(value);
    }
}
