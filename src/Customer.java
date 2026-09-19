import java.util.Scanner;

public class Customer extends User{
    private Account savingsAccount;
    private Account checkingAccount;

    public Customer(String id, String name, String password, String phoneNumber,double savingsBalance, double checkingBalance) {
        super(id, name, password, phoneNumber,"Customer");
        this.savingsAccount = new Account("Savings",savingsBalance);
        this.checkingAccount = new Account("Checking",checkingBalance);
    }
public Account getSavingsAccount(){
        return savingsAccount;
}
public Account getCheckingAccount(){
        return checkingAccount;
}

@Override
    public String toFileString(){
        return  getId() +"|" + getRole() + "|" + getName() + "|" + getPassword() + "|" +getPhoneNumber() + "|" + "S" +savingsAccount.getBalance() + "|" + "C"+checkingAccount.getBalance();
}


     }


