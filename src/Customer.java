import java.util.Scanner;

public class Customer extends User {
    private Account savingsAccount;
    private Account checkingAccount;

    public Customer(String id, String name, String password, String phoneNumber,
                    Account savingsAccount, Account checkingAccount) {
        super(id, name, password, phoneNumber, "Customer");
        this.savingsAccount = savingsAccount;
        this.checkingAccount = checkingAccount;
    }

    public Account getSavingsAccount() {
        return savingsAccount;
    }

    public Account getCheckingAccount() {
        return checkingAccount;
    }

    public boolean hasSavings() {
        return savingsAccount != null;
    }

    public boolean hasChecking() {
        return checkingAccount != null;
    }

    @Override
    public String toFileString() {
        String savingsPart = hasSavings()
                ? savingsAccount.getBalance() + ":" + savingsAccount.getOverdraftCount() + ":" + savingsAccount.isActive() + ":" + savingsAccount.getCardType()
                : "NONE";
        String checkingPart = hasChecking()
                ? checkingAccount.getBalance() + ":" + checkingAccount.getOverdraftCount() + ":" + checkingAccount.isActive() + ":" + checkingAccount.getCardType()
                : "NONE";

        return getRole() + "|" + getId() + "|" + getName() + "|" + getPassword() + "|"
                + getPhoneNumber() + "|" + savingsPart + "|" + checkingPart;
    }
}