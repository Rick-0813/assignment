import java.util.ArrayList;

public class LibraryManager {
    private ArrayList<AdminLibrary> userList;

    public LibraryManager() {
        this.userList = new ArrayList<>();
    }

    public void addUser(AdminLibrary newUser) {
        userList.add(newUser);
        System.out.println("System: User [" + newUser.getAdminName() + "] has been added to the list.");
    }

    public void displayAllUsers() {
        if (userList.isEmpty()) {
            System.out.println("no users found.");
            return;
        }
        System.out.println("\n--- User List ---");
        for (AdminLibrary u : userList) {
            System.out.println("ID: " + u.getUserID() + " | Name: " + u.getAdminName() + " | Type: " + u.getUserType());
        }
    }

    public void findUser(String id) {
        for (AdminLibrary user : userList) {
            if (user.getUserID().equals(id)) {
                System.out.println("Find User: " + user.getAdminName());
                return;
            }
        }
        System.out.println("Invalid User");
    }
}