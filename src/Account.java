public class Account {
    private String type;
    private double balance;
    private int overdraftCount;
    private boolean isActive;

    public Account(String type, double balance) {
      this.type = type;
      this.balance = balance;
      this.overdraftCount = 0;
      this.isActive = true;
    }
    public Account(String type, double balance, int overdraftCount, boolean isActive) {
        this.type = type;
        this.balance = balance;
        this.overdraftCount = overdraftCount;
        this.isActive = isActive;
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
    public int getOverdraftCount() {
        return overdraftCount;
    }
    public void setOverdraftCount(int overdraftCount) {
        this.overdraftCount = overdraftCount;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
}
