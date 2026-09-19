public class Account {
    private String type;
    private double balance;

    public Account(String type, double balance){
        this.type = type;
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
