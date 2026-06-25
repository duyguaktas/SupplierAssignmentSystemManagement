import com.google.gson.annotations.SerializedName;

public enum SupplierType {
    @SerializedName("1")
    CONTRACT_ONLY(1, "Type 1: Only Contract"),
    @SerializedName("2")
    STOCK_ONLY(2, "Type 2: Only Stock"),
    @SerializedName("3")
    BOTH(3, "Type 3: Both Contract & Stock");

    private final int value;
    private final String description;

    SupplierType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static SupplierType fromInt(int value) {
        for (SupplierType type : SupplierType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return CONTRACT_ONLY; // Default
    }
}
