import java.util.Scanner;

public class Banker extends  User {
    public Banker(String id,String name,String password,String phoneNumber){
        super(id, name,password,phoneNumber,"Banker");
    }
@Override

    public String toFileString(){
        return getRole() + "|"+ getId() + "|" + getName() + "|" + getPassword()+ "|" + getPhoneNumber();

}
}
