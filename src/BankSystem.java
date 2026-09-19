//import java.util.Scanner;
//
//public class BankSystem {
//    public static void main(String[] args) {
//        // Run the start function
//        start();
//    }
//
//    public static void start(){
//        Scanner scanner = new Scanner(System.in);
//        Banker defaultBanker = new Banker("admin","banker123","EMP-1001");
//        Customer defaultCustomer = new Customer("Mohammed_ali", "pass123");
//
//        System.out.println("==================================");
//        System.out.println("    WELCOME TO THE BANK PORTAL    ");
//        System.out.println("==================================");
//        System.out.println("1. Login as Banker");
//        System.out.println("2. Login as Customer");
//        System.out.print("Choose role (1 or 2): ");
//
//        int roleChoice = scanner.nextInt();
//        scanner.nextLine();
//
//        System.out.println("Enter username: ");
//        String username = scanner.nextLine();
//        System.out.println("Enter password: ");
//        String password = scanner.nextLine();
//
//        if (roleChoice == 1){
//            if (defaultBanker.getUsername().equalsIgnoreCase(username)&& defaultBanker.authenticate(password)) {
//                System.out.println("\nLogin Successful!");
//                defaultBanker.showDashboard(scanner);
//
//            }else {
//                System.out.println("\nInvalid Banker credentials.");
//            }
//            if (roleChoice == 2){
//                if (defaultCustomer.getUsername().equalsIgnoreCase(username)&& defaultCustomer.authenticate(password)){
//                    System.out.println("\nLogin Successful");
//                    defaultCustomer.chooseAccount(scanner);
//                    defaultCustomer.showDashboard(scanner);
//                } else {
//                    System.out.println("\nInvalid Customer credentials.");
//                }
//            } else {
//                System.out.println("Invalid role choice.");
//            }
//
//            scanner.close();
//        }
//    }
//                }
//
//
//
