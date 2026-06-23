import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AssignmentGenerator {

    private List<Supplier> type1Suppliers = new ArrayList<>();
    private List<Supplier> type2Suppliers = new ArrayList<>();
    private List<Supplier> type3Suppliers = new ArrayList<>();

    private String lastContractSupplier = "";
    private String lastStockSupplier = "";

    public AssignmentGenerator() {}

    public void sortSuppliers(List<Supplier> suppliers) {
        type1Suppliers.clear();
        type2Suppliers.clear();
        type3Suppliers.clear();

        for (Supplier supplier : suppliers) {
            int typeNo = supplier.getType();
            if (typeNo == 1) {
                type1Suppliers.add(supplier);
            } else if (typeNo == 2) {
                type2Suppliers.add(supplier);
            } else if (typeNo == 3) {
                type3Suppliers.add(supplier);
            } else {
                throw new IllegalArgumentException("Invalid supplier type: " + typeNo);
            }
        }
    }

    private boolean isReserved(Supplier s, int day) {
        String reservedString = s.getReservedDays();
        if (reservedString == null || reservedString.trim().isEmpty()) {
            return false;
        }

        String[] daysArray = reservedString.split(",");
        for (String dayStr : daysArray) {
            try {
                if (Integer.parseInt(dayStr.trim()) == day) {
                    return true;
                }
            } catch (NumberFormatException ignored) {}
        }
        return false;
    }

    private Supplier findBestAvailable(List<Supplier> pool, int day, String lastC, String lastS) {
        Supplier best = null;
        int minWork = Integer.MAX_VALUE;

        for (Supplier supplier : pool) {
            // Check availability and consecutive day rule
            if (!isReserved(supplier, day) && !supplier.getInfo().equals(lastC) && !supplier.getInfo().equals(lastS)) {
                // Choose the one with the least work count
                if (supplier.getWorkCount() < minWork) {
                    minWork = supplier.getWorkCount();
                    best = supplier;
                }
            }
        }
        return best;
    }


    public List<Assignment> generateAssignments(List<Supplier> suppliers) {
        sortSuppliers(suppliers);
        List<Assignment> assignments = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);


        for (int day = 1; day <= daysInMonth; day++) {
            Supplier s1 ;
            Supplier s2 ;

            if (day % 2 != 0) { // match type 3 suppliers with odd days
                s1 = findBestAvailable(type3Suppliers, day, lastContractSupplier, lastStockSupplier);
                s2 = s1;

                // if not available, pick a type 1 & type 2 pair
                if (s1 == null) {
                    s1 = findBestAvailable(type1Suppliers, day, lastContractSupplier, lastStockSupplier);
                    s2 = findBestAvailable(type2Suppliers, day, lastContractSupplier, lastStockSupplier);
                }
            } else { // pair type 1 & type 2 suppliers for even days
                s1 = findBestAvailable(type1Suppliers, day, lastContractSupplier, lastStockSupplier);
                s2 = findBestAvailable(type2Suppliers, day, lastContractSupplier, lastStockSupplier);

                // if not available, match with a type 3 supplier instead
                if (s1 == null || s2 == null) {
                    s1 = findBestAvailable(type3Suppliers, day, lastContractSupplier, lastStockSupplier);
                    s2 = s1;
                }
            }

            if (s1 != null && s2 != null) {
                assignments.add(new Assignment(s1.getInfo(), s2.getInfo(), day, month));

                s1.incWorkCount();
                if (s1 != s2) {
                    s2.incWorkCount();
                }

                // remember who worked today to prevent them working tomorrow
                lastContractSupplier = s1.getInfo();
                lastStockSupplier = s2.getInfo();
            } else {
                // if no one is available for a day, reset history so anyone can work next day
                lastContractSupplier = "";
                lastStockSupplier = "";
            }
        }

        return assignments;
    }
}