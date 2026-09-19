import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        UserFileManager fileManager = new UserFileManager();
        List<User> allUsers = fileManager.loadAllUsers();
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


    private static void registerNewUser(List<User> allUsers, Scanner scanner, UserFileManager fileManager) throws
            IOException {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter phone Number: ");
        String phone = scanner.nextLine();

        System.out.print("Are you Customer or Banker? ");
        String type = scanner.nextLine();

        User newUser;

        if (type.equalsIgnoreCase("Customer")) {
            System.out.print("Enter initial Saving balance: ");
            double savingsbalance = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter initial checking balance: ");
            double checkingBalance = Double.parseDouble(scanner.nextLine());

            newUser = new Customer(id, name, password, phone, savingsbalance, checkingBalance);
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
                System.out.print("Enter amount to deposit: ");
                double amount = Double.parseDouble(scanner.nextLine());

                account.setBalance(account.getBalance() + amount);
                fileManager.saveUser(customer);

                System.out.println("Deposit successful . new balance: " + account.getBalance());

            } else if (choice == 2) {
                Account account = chooseAccount(customer, scanner);
                System.out.print("Enter amount to withdraw: ");
                double amount = Double.parseDouble(scanner.nextLine());

                if (amount > account.getBalance()) {
                    System.out.println("Insufficient balance.");
                } else {
                    account.setBalance(account.getBalance() - amount);
                    fileManager.saveUser(customer);
                    System.out.println("Witdraw successful. New balance: " + account.getBalance());
                }
            }
            if (choice == 3) {
                System.out.println("Savings balance: " + customer.getSavingsAccount().getBalance());
                System.out.println("Checking balance: " + customer.getCheckingAccount().getBalance());
            } else if (choice == 4) {
                System.out.println("Transfer - coming next step");
            } else if (choice == 5) {
                loggedIn = false;
            }

        }
    }

    private static Account chooseAccount(Customer customer, Scanner scanner) {
        System.out.println("Choose account (Savings/Checking): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("Savings")) {
            return customer.getSavingsAccount();
        } else {
            return customer.getCheckingAccount();
        }
    }
        private static void bankerMenu (Banker banker, Scanner scanner){
            System.out.println("Banker menu - coming soon");
        }

    }


