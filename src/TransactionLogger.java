import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {
    private String directoryPath = "transactions";

    public TransactionLogger() {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void log(String customerId, String type, double amount, double resultingBalance) throws IOException {
        String fileName = directoryPath + File.separator + "Customer-" + customerId + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = timestamp + "|" + type + "|" + amount + "|" + resultingBalance;

        writer.write(line);
        writer.newLine();
        writer.close();
    }

    public void printHistory(String customerId) throws IOException {
        String fileName = directoryPath + File.separator + "Customer-" + customerId + ".txt";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("No transactions yet.");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        System.out.println("---- Transaction History ----");
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            System.out.println(parts[0] + " | " + parts[1] + " | Amount: " + parts[2] + " | Balance after: " + parts[3]);
        }
        reader.close();
    }

    public double getTodayTotal(String customerId, String type) throws IOException {
        String fileName = directoryPath + File.separator + "Customer-" + customerId + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            return 0;
        }

        String today = java.time.LocalDate.now().toString();
        double total = 0;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String date = parts[0].substring(0, 10);
            String logType = parts[1];
            double amount = Double.parseDouble(parts[2]);

            if (date.equals(today) && logType.equals(type)) {
                total += amount;
            }
        }
        reader.close();
        return total;
    }

    public static double getLimit(String cardType, String operation) {
        if (cardType.equals("Platinum")) {
            if (operation.equals("Withdraw")) return 20000;
            if (operation.equals("Transfer")) return 40000;
            if (operation.equals("Transfer-Own")) return 80000;
            if (operation.equals("Deposit")) return 100000;
        } else if (cardType.equals("Titanium")) {
            if (operation.equals("Withdraw")) return 10000;
            if (operation.equals("Transfer")) return 20000;
            if (operation.equals("Transfer-Own")) return 40000;
            if (operation.equals("Deposit")) return 100000;
        } else {
            if (operation.equals("Withdraw")) return 5000;
            if (operation.equals("Transfer")) return 10000;
            if (operation.equals("Transfer-Own")) return 20000;
            if (operation.equals("Deposit")) return 100000;
        }
        return 0;
    }
    public void printFiltered(String customerId, int daysBack) throws IOException {
        String fileName = directoryPath + File.separator + "Customer-" + customerId + ".txt";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("No transactions yet.");
            return;
        }

        java.time.LocalDate cutoff = java.time.LocalDate.now().minusDays(daysBack);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        System.out.println("---- Filtered Transactions ----");
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String dateStr = parts[0].substring(0, 10);
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);

            if (!date.isBefore(cutoff)) {
                System.out.println(parts[0] + " | " + parts[1] + " | Amount: " + parts[2] + " | Balance after: " + parts[3]);
                found = true;
            }
        }
        reader.close();

        if (!found) {
            System.out.println("No transactions in this period.");
        }
    }
}