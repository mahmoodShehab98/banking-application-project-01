import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {
    private static Map<String, Integer> failedAttempts = new HashMap<>();
    private static Map<String, Long> lockTime = new HashMap<>();

    public static void main(String[] args) throws IOException {
        UserFileManager fileManager = new UserFileManager();
        TransactionLogger transactionLogger = new TransactionLogger();
        List<User> allUsers = fileManager.loadAllUsers();

        System.out.println("||=-=-=-=-ACME BANK-=-=-=-=||");
//        System.out.println("Loaded users:" + allUsers.size());
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            System.out.println("--~ACME BANK~--");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                registerNewUser(allUsers, scanner, fileManager);
            } else if (choice == 2) {
                loginUser(allUsers, scanner, fileManager, transactionLogger);
            } else if (choice == 3) {
                running = false;
            } else {
                System.out.println("Invalid choice");
            }
        }
        System.out.println("Goodbye!!");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerNewUser(List<User> allUsers, Scanner scanner, UserFileManager fileManager) throws IOException {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        password = hashPassword(password);

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
                String savingsCard = chooseCardType(scanner);
                savingsAccount = new Account("Savings", savingsBalance, savingsCard);
            }

            if (wantsChecking) {
                System.out.print("Enter initial Checking balance: ");
                double checkingBalance = Double.parseDouble(scanner.nextLine());
                String checkingCard = chooseCardType(scanner);
                checkingAccount = new Account("Checking", checkingBalance, checkingCard);
            }

            newUser = new Customer(id, name, password, phone, savingsAccount, checkingAccount);

        } else {
            newUser = new Banker(id, name, password, phone);
        }
        allUsers.add(newUser);
        fileManager.saveUser(newUser);

        System.out.println("Account created successfully !");
    }

    private static String chooseCardType(Scanner scanner) {
        System.out.println("Choose card type:");
        System.out.println("1) Mastercard Platinum");
        System.out.println("2) Mastercard Titanium");
        System.out.println("3) Mastercard");
        System.out.print("Choose (1/2/3): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            return "Platinum";
        } else if (choice.equals("2")) {
            return "Titanium";
        } else {
            return "Mastercard";
        }
    }

    private static void loginUser(List<User> allUsers, Scanner scanner, UserFileManager fileManager, TransactionLogger transactionLogger) throws IOException {
        System.out.print("ID: ");
        String id = scanner.nextLine();

        if (lockTime.containsKey(id)) {
            long elapsed = System.currentTimeMillis() - lockTime.get(id);
            if (elapsed < 60000) {
                long remaining = (60000 - elapsed) / 1000;
                System.out.println("Account locked due to too many failed attempts. Try again in " + remaining + " seconds.");
                return;
            } else {
                lockTime.remove(id);
                failedAttempts.remove(id);
            }
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String hashedInput = hashPassword(password);

        for (User u : allUsers) {
            if (u.getId().equals(id) && u.getPassword().equals(hashedInput)) {
                failedAttempts.remove(id);
                System.out.println("Login successful! welcome " + u.getName());

                if (u instanceof Customer) {
                    Customer customer = (Customer) u;
                    customerMenu(customer, scanner, allUsers, fileManager, transactionLogger);
                } else if (u instanceof Banker) {
                    Banker banker = (Banker) u;
                    bankerMenu(banker, scanner,allUsers,transactionLogger);
                }

                return;
            }
        }
        int attempts = failedAttempts.getOrDefault(id, 0) + 1;
        failedAttempts.put(id, attempts);
        if (attempts >= 3) {
            lockTime.put(id, System.currentTimeMillis());
            System.out.println("Too many failed attempts. Account locked for 1 minute.");
        } else {
            System.out.println("Invalid ID or password. Attempts remaining: " + (3 - attempts));
        }
    }

    private static void customerMenu(Customer customer, Scanner scanner, List<User> allUsers, UserFileManager fileManager, TransactionLogger transactionLogger) throws IOException {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Transfer");
            System.out.println("5. Logout");
            System.out.println("6. View Transaction History");
            System.out.println("7. Filter Transactions");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                Account account = chooseAccount(customer, scanner);
                if (account == null) {
                    continue;
                }
                System.out.print("Enter amount to deposit: ");
                double amount = Double.parseDouble(scanner.nextLine());

                double todayDeposits = transactionLogger.getTodayTotal(customer.getId(), "Deposit");
                double depositLimit = TransactionLogger.getLimit(account.getCardType(), "Deposit");
                if (todayDeposits + amount > depositLimit) {
                    System.out.println("Daily deposit limit exceeded for " + account.getCardType() + " card ($" + depositLimit + "). Already deposited today: $" + todayDeposits);
                    continue;
                }

                account.setBalance(account.getBalance() + amount);

                if (account.getBalance() >= 0 && !account.isActive()) {
                    account.setActive(true);
                    account.setOverdraftCount(0);
                    System.out.println("Account has been reactivated.");
                }

                fileManager.saveUser(customer);
                transactionLogger.log(customer.getId(), "Deposit", amount, account.getBalance());

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

                double todayWithdrawals = transactionLogger.getTodayTotal(customer.getId(), "Withdraw");
                double withdrawLimit = TransactionLogger.getLimit(account.getCardType(), "Withdraw");
                if (todayWithdrawals + amount > withdrawLimit) {
                    System.out.println("Daily withdraw limit exceeded for " + account.getCardType() + " card ($" + withdrawLimit + "). Already withdrawn today: $" + todayWithdrawals);
                    continue;
                }

                double newBalance = account.getBalance() - amount;

                if (newBalance < 0) {
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
                transactionLogger.log(customer.getId(), "Withdraw", amount, account.getBalance());

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

                        double todayOwnTransfers = transactionLogger.getTodayTotal(customer.getId(), "Transfer-Own-Out");
                        double ownTransferLimit = TransactionLogger.getLimit(from.getCardType(), "Transfer-Own");
                        if (todayOwnTransfers + amount > ownTransferLimit) {
                            System.out.println("Daily own-account transfer limit exceeded for " + from.getCardType() + " card ($" + ownTransferLimit + ").");
                            continue;
                        }

                        if (amount > from.getBalance()) {
                            System.out.println("Insufficient balance.");
                        } else {
                            from.setBalance(from.getBalance() - amount);
                            to.setBalance(to.getBalance() + amount);
                            fileManager.saveUser(customer);
                            transactionLogger.log(customer.getId(), "Transfer-Own-Out", amount, from.getBalance());
                            transactionLogger.log(customer.getId(), "Transfer-In", amount, to.getBalance());
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

                        double todayTransfers = transactionLogger.getTodayTotal(customer.getId(), "Transfer-Out");
                        double transferLimit = TransactionLogger.getLimit(from.getCardType(), "Transfer");
                        if (todayTransfers + amount > transferLimit) {
                            System.out.println("Daily transfer limit exceeded for " + from.getCardType() + " card ($" + transferLimit + ").");
                            continue;
                        }

                        if (amount > from.getBalance()) {
                            System.out.println("Insufficient balance.");
                        } else {
                            from.setBalance(from.getBalance() - amount);
                            to.setBalance(to.getBalance() + amount);
                            fileManager.saveUser(customer);
                            fileManager.saveUser(recipient);
                            transactionLogger.log(customer.getId(), "Transfer-Out", amount, from.getBalance());
                            transactionLogger.log(recipient.getId(), "Transfer-In", amount, to.getBalance());
                            System.out.println("Transfer successful. your balance now " + from.getBalance());
                        }
                    }
                }

            } else if (choice == 6) {
                transactionLogger.printHistory(customer.getId());

            } else if (choice == 7) {
                System.out.println("1) Today");
                System.out.println("2) Last 7 days");
                System.out.println("3) Last 30 days");
                System.out.print("Choose: ");
                String filterChoice = scanner.nextLine().trim();

                if (filterChoice.equals("1")) {
                    transactionLogger.printFiltered(customer.getId(), 0);
                } else if (filterChoice.equals("2")) {
                    transactionLogger.printFiltered(customer.getId(), 7);
                } else if (filterChoice.equals("3")) {
                    transactionLogger.printFiltered(customer.getId(), 30);
                } else {
                    System.out.println("Invalid choice.");
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

    private static void bankerMenu(Banker banker, Scanner scanner, List<User> allUsers, TransactionLogger transactionLogger) throws IOException {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("1. View all customers");
            System.out.println("2. Search customer by ID");
            System.out.println("3. View customer transaction history");
            System.out.println("4. Logout");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                System.out.println("---- All Customers ----");
                for (User u : allUsers) {
                    if (u instanceof Customer) {
                        Customer c = (Customer) u;
                        System.out.println("ID: " + c.getId() + " | Name: " + c.getName());
                        if (c.hasSavings()) {
                            System.out.println("   Savings: $" + c.getSavingsAccount().getBalance() + " | Card: " + c.getSavingsAccount().getCardType() + " | Active: " + c.getSavingsAccount().isActive());
                        }
                        if (c.hasChecking()) {
                            System.out.println("   Checking: $" + c.getCheckingAccount().getBalance() + " | Card: " + c.getCheckingAccount().getCardType() + " | Active: " + c.getCheckingAccount().isActive());
                        }
                    }
                }

            } else if (choice == 2) {
                System.out.print("Enter customer ID: ");
                String id = scanner.nextLine().trim();
                Customer found = findCustomerById(allUsers, id);
                if (found == null) {
                    System.out.println("Customer not found.");
                } else {
                    System.out.println("ID: " + found.getId() + " | Name: " + found.getName() + " | Phone: " + found.getPhoneNumber());
                    if (found.hasSavings()) {
                        System.out.println("Savings: $" + found.getSavingsAccount().getBalance() + " | Card: " + found.getSavingsAccount().getCardType() + " | Active: " + found.getSavingsAccount().isActive() + " | Overdrafts: " + found.getSavingsAccount().getOverdraftCount());
                    }
                    if (found.hasChecking()) {
                        System.out.println("Checking: $" + found.getCheckingAccount().getBalance() + " | Card: " + found.getCheckingAccount().getCardType() + " | Active: " + found.getCheckingAccount().isActive() + " | Overdrafts: " + found.getCheckingAccount().getOverdraftCount());
                    }
                }

            } else if (choice == 3) {
                System.out.print("Enter customer ID: ");
                String id = scanner.nextLine().trim();
                Customer found = findCustomerById(allUsers, id);
                if (found == null) {
                    System.out.println("Customer not found.");
                } else {
                    transactionLogger.printHistory(found.getId());
                }

            } else if (choice == 4) {
                loggedIn = false;
            }
        }
    }
}