public class Assignment {

    private int id;

    private String contractSupplier;
    private String stockSupplier;
    private int dayOfTheMonth;
    private int month;

    public Assignment(String contractSupplier, String stockSupplier, int dayOfTheMonth, int month) {
        setContractSupplier(contractSupplier);
        setStockSupplier(stockSupplier);
        setDayOfTheMonth(dayOfTheMonth);
        setMonth(month);
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getContractSupplier() {return contractSupplier;}
    public void setContractSupplier(String contractSupplier) {this.contractSupplier = contractSupplier;}

    public String getStockSupplier() {return stockSupplier;}
    public void setStockSupplier(String stockSupplier) {this.stockSupplier = stockSupplier;}

    public int getDayOfTheMonth() {return dayOfTheMonth;}
    public void setDayOfTheMonth(int dayOfTheMonth) {this.dayOfTheMonth = dayOfTheMonth;}

    public int getMonth() {return month;}
    public void setMonth(int month) {this.month = month;}
}
