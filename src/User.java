import java.util.Scanner;

abstract class User {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
    private String role;


    public User(String id, String name, String password, String phoneNumber, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;

    }




    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }
    public abstract String toFileString();

    }
    

