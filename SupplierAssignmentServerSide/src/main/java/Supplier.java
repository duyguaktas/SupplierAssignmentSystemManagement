
public class Supplier {

    private int id;
    private String info;
    private SupplierType type;
    private String reservedDays;
    private int workCount = 0;

    public Supplier(int id, String supplierInfo, SupplierType supplierType, String lastReservedDays) {
        setId(id);
        setInfo(supplierInfo);
        setType(supplierType);
        setReservedDays(lastReservedDays);
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getInfo() {return info;}

    public void setInfo(String supplierInfo) {this.info = supplierInfo;}

    public SupplierType getType() {return type;}

    public void setType(SupplierType supplierType) {
        this.type = supplierType;
    }

    public String getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(String lastReservedDays) {
        this.reservedDays = formatReservedDays(lastReservedDays);
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

    public int getWorkCount() { return workCount; }

    public void incWorkCount() { this.workCount++; }

    @Override
    public String toString() {
        return info + " (Type " + type + ")";
    }

}
