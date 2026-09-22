import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        UserFileManager fileManager = new UserFileManager();
        List<User> allUsers = fileManager.loadAllUsers();

        System.out.println("Loaded users:" + allUsers.size());
        Scanner scanner = new Scanner(System.in);

        boolean running = true;


        while (running) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                registerNewUser(allUsers, scanner, fileManager);
            } else if (choice == 2) {
                loginUser(allUsers, scanner, fileManager);
            } else if (choice == 3) {
                running = false;
            } else {
                System.out.println("Invalid choice");
            }
        }
        System.out.println("Goodbye!!");
    }


    private static void registerNewUser(List<User> allUsers, Scanner scanner, UserFileManager fileManager) throws IOException {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter phone Number: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Are you Customer or Banker? ");
        String type = scanner.nextLine().trim();

        User newUser;

        if (type.equalsIgnoreCase("Customer")) {
            System.out.println("Which account(s) do you want to open?");
            System.out.println("1) Savings");
            System.out.println("2) Checking");
            System.out.println("3) Both");
            System.out.print("Choose (1/2/3): ");
            String choice = scanner.nextLine().trim();

            boolean wantsSavings = choice.equals("1") || choice.equals("3");
            boolean wantsChecking = choice.equals("2") || choice.equals("3");

            Account savingsAccount = null;
            Account checkingAccount = null;

            if (wantsSavings) {
                System.out.print("Enter initial Savings balance: ");
                double savingsBalance = Double.parseDouble(scanner.nextLine());
                savingsAccount = new Account("Savings", savingsBalance);
            }

            if (wantsChecking) {
                System.out.print("Enter initial Checking balance: ");
                double checkingBalance = Double.parseDouble(scanner.nextLine());
                checkingAccount = new Account("Checking", checkingBalance);
            }

            newUser = new Customer(id, name, password, phone, savingsAccount, checkingAccount);

        } else {
            newUser = new Banker(id, name, password, phone);
        }
        allUsers.add(newUser);
        fileManager.saveUser(newUser);

        System.out.println("Account created successfully !");
    }

    private static void loginUser(List<User> allUsers, Scanner scanner, UserFileManager fileManager) throws IOException {
        System.out.print("ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User u : allUsers) {
            if (u.getId().equals(id) && u.getPassword().equals(password)) {
                System.out.println("Login successful! welcome " + u.getName());

                if (u instanceof Customer) {
                    Customer customer = (Customer) u;
                    customerMenu(customer, scanner, allUsers, fileManager);
                } else if (u instanceof Banker) {
                    Banker banker = (Banker) u;
                    bankerMenu(banker, scanner);

                }

                return;
            }
        }
        System.out.println("Invalid ID or password");
    }

    private static void customerMenu(Customer customer, Scanner scanner, List<User> allUsers, UserFileManager fileManager) throws IOException {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Transfer");
            System.out.println("5. Logout");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                Account account = chooseAccount(customer, scanner);
                if (account == null) {
                    continue;
                }
                System.out.print("Enter amount to deposit: ");
                double amount = Double.parseDouble(scanner.nextLine());

                account.setBalance(account.getBalance() + amount);

                if (account.getBalance() >= 0 && !account.isActive()) {
                    account.setActive(true);
                    account.setOverdraftCount(0);
                    System.out.println("Account has been reactivated.");
                }
                fileManager.saveUser(customer);

                System.out.println("Deposit successful. New balance: " + account.getBalance());

            } else if (choice == 2) {
                Account account = chooseAccount(customer, scanner);
                if (account == null) {
                    continue;
                }
                if (!account.isActive()) {
                    System.out.println("This account is deactivated due to repeated overdrafts. Please deposit to resolve.");
                    continue;
                }

                System.out.print("Enter amount to withdraw: ");
                double amount = Double.parseDouble(scanner.nextLine());

                boolean wasNegative = account.getBalance() < 0;

                if (wasNegative && amount > 100) {
                    System.out.println("Cannot withdraw more than $100 while account balance is negative.");
                    continue;
                }

                double newBalance = account.getBalance() - amount;

                if (newBalance < 0 && !wasNegative) {
                    newBalance -= 35;
                    account.setOverdraftCount(account.getOverdraftCount() + 1);
                    System.out.println("Overdraft occurred. A $35 fee has been charged.");

                    if (account.getOverdraftCount() >= 2) {
                        account.setActive(false);
                        System.out.println("Account has been deactivated due to repeated overdrafts.");
                    }
                }

                account.setBalance(newBalance);
                fileManager.saveUser(customer);
                System.out.println("Withdraw successful. New balance: " + account.getBalance());


            } else if (choice == 3) {
                if (customer.hasSavings()) {
                    System.out.println("Savings balance: " + customer.getSavingsAccount().getBalance());
                } else {
                    System.out.println("Savings: no account.");
                }
                if (customer.hasChecking()) {
                    System.out.println("Checking balance: " + customer.getCheckingAccount().getBalance());
                } else {
                    System.out.println("Checking: no account.");
                }

            } else if (choice == 4) {
                System.out.println("1. Transfer to my own account");
                System.out.println("2. Transfer to another account");
                int transferChoise = Integer.parseInt(scanner.nextLine());

                if (transferChoise == 1) {
                    System.out.println("Transfer from:");
                    Account from = chooseAccount(customer, scanner);
                    if (from == null) {
                        continue;
                    }

                    System.out.println("Transfer to:");
                    Account to = chooseAccount(customer, scanner);
                    if (to == null) {
                        continue;
                    }

                    if (from == to) {
                        System.out.println("Cannot transfer to the same account.");
                    } else {
                        System.out.print("Enter amount to transfer: ");
                        double amount = Double.parseDouble(scanner.nextLine());

                        if (amount > from.getBalance()) {
                            System.out.println("Insufficient balance.");
                        } else {
                            from.setBalance(from.getBalance() - amount);
                            to.setBalance(to.getBalance() + amount);
                            fileManager.saveUser(customer);
                            System.out.println("Transfer successful. New balance: " + from.getBalance());
                        }
                    }

                } else if (transferChoise == 2) {
                    System.out.println("Enter recipient customer ID: ");
                    String recipientId = scanner.nextLine().trim();

                    Customer recipient = findCustomerById(allUsers, recipientId);
                    if (recipient == null) {
                        System.out.println("Customer not found.");
                        continue;
                    }
                    if (recipient == customer) {
                        System.out.println("Use option 1 to transfer to your own accounts.");
                        continue;
                    }

                    System.out.println("Transfer from your account:");
                    Account from = chooseAccount(customer, scanner);
                    if (from == null) {
                        continue;
                    }

                    System.out.println("Transfer to recipient account:");
                    Account to = chooseAccount(recipient, scanner);
                    if (to == null) {
                        continue;
                    }

                    if (from == to) {
                        System.out.println("Cannot transfer to the same account.");
                    } else {
                        System.out.print("Enter amount to transfer: ");
                        double amount = Double.parseDouble(scanner.nextLine());

                        if (amount > from.getBalance()) {
                            System.out.println("Insufficient balance.");
                        } else {
                            from.setBalance(from.getBalance() - amount);
                            to.setBalance(to.getBalance() + amount);
                            fileManager.saveUser(customer);
                            fileManager.saveUser(recipient);
                            System.out.println("Transfer successful." + "your balance now " +from.getBalance());
                        }
                    }
                }

            } else if (choice == 5) {
                loggedIn = false;
            }
        }
    }
    private static Account chooseAccount(Customer customer, Scanner scanner) {
        System.out.println("Choose account (Savings/Checking): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("Savings")) {
            if (!customer.hasSavings()) {
                System.out.println("You don't have a Savings account.");
                return null;
            }
            return customer.getSavingsAccount();
        } else {
            if (!customer.hasChecking()) {
                System.out.println("You don't have a Checking account");
                return null;
            }
            return customer.getCheckingAccount();
        }
    }

    private static Customer findCustomerById(List<User> allUser, String id) {
        for (User user : allUser) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                if (customer.getId().equals(id)) {
                    return customer;
                }
            }
        }
        return null;
    }


    private static void bankerMenu(Banker banker, Scanner scanner) {
        System.out.println("Banker menu - coming soon");

    }
}


