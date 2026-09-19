import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class UserFileManager {
    private String directoryPath = "user";

    public UserFileManager() {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        System.out.println("Working dir: " + new File(".").getAbsolutePath());
    }

    private String getFileName(User u) {
        return u.getRole() + "-" + u.getName() + "-" + u.getId() + ".txt";
    }

    public void saveUser(User u) throws IOException {
        String fullPath = directoryPath + File.separator + getFileName(u);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
        writer.write(u.toFileString());
        writer.newLine();
        writer.close();
    }

    public List<User> loadAllUsers() throws IOException {
        List<User> allUsers = new ArrayList<>();
        File dir = new File(directoryPath);

        File[] files = dir.listFiles();
        if (files == null) {
            return allUsers;
        }
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".txt")){
                continue;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();

            if (line == null) {
                continue;
            }
            String[] parts = line.split("\\|");
            String role = parts[0];

            if (role.equals("Customer")) {
                String id = parts[1];
                String name = parts[2];
                String password = parts[3];
                String phone = parts[4];
                double savingsBalance = Double.parseDouble(parts[5]);
                double checkingBalance = Double.parseDouble(parts[6]);

                allUsers.add(new Customer(id, name, password, phone, savingsBalance, checkingBalance));


            } else if (role.equals("Banker")) {
                String id = parts[1];
                String name = parts[2];
                String password = parts[3];
                String phone = parts[4];

                allUsers.add(new Banker(id, name, password, phone));
            }

        }
        return allUsers;

    }
}

//    public List<User> loadAllUsers() throws IOException {
//        List<User> allUsers = new ArrayList<>();
//        File file = new File(filePath);
//
//        if (!file.exists()) {
//            return allUsers;
//        }
//        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            String[] parts = line.split("\\|");
//            String role = parts[0];
//
//            if (role.equals("Customer")) {
//                String id = parts[1];
//                String name = parts[2];
//                String password = parts[3];
//                String phone = parts[4];
//                String accountType = parts[5];
//                double balance = Double.parseDouble(parts[6]);
//
//                allUsers.add(new Customer(id, name, password, phone, accountType, balance));
//            } else if (role.equals("Banker")) {
//                String id = parts[1];
//                String name = parts[2];
//                String password = parts[3];
//                String phone = parts[4];
//
//                allUsers.add(new Banker(id, name, password, phone));
//            }
//
//        }
//        reader.close();
//        return allUsers;
//    }
//
//    public void saveAllUsers(List<User>allUsers)throws IOException{
//        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
//
//        for (User u : allUsers){
//            writer.write(u.toFileString());
//            writer.newLine();
//        }
//        writer.close();
//    }
//}

