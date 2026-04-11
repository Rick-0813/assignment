import java.util.*;
import java.io.*;

public class StaticUser {
    private static ArrayList<Account> accounts = new ArrayList<>();
    private static final String FILE_NAME = "accounts.txt";
    private static String currentUserType = null;
    private static String currentUserID = null;

    public static String getCurrentUserType() {
        return currentUserType;
    }

    public static String getCurrentUserID() {
        return currentUserID;
    }

    public static void logout(){
        currentUserType = null;
        currentUserID = null;
    }

    static {
        loadAccounts();
        if (accounts.isEmpty()) accounts.add(new Account("admin", "1234", "Admin"));
    }

    static class Account {
        private String username;
        private String password;
        private String type;

        Account(String u, String p, String t) { 
            this.username = u; 
            this.password = p; 
            this.type = t; 
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getType() { return type; }
    }

    public static String loginProcess(Scanner input, LibraryManager manager){
        while (true) {
            LibraryUI.printLoginMenu();
            System.out.print("  Please select your role: ");
            
            if (!input.hasNextInt()) {
                input.nextLine();
                System.out.println("  Invalid input!");
                continue;
            }
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) { 
                LibraryUI.printGoodbye();
                return null;
            } else if (choice >= 1 && choice <= 3) {
                String[] roles = {"Student", "Faculty", "Public Member"}; 
                System.out.print("  Please enter your ID: ");
                String typedID = input.nextLine().trim();
                AdminLibrary userInDB = manager.getUserByID(typedID);

                if (userInDB == null) {
                    System.out.println("\n  [!] Access Denied: User ID [" + typedID + "] not found!");
                    System.out.println("      If you are a new user, please contact the Librarian (Admin) to register.\n");
                    continue; 
                }

                String expectedRole = roles[choice - 1];
                if (!userInDB.getUserType().equalsIgnoreCase(expectedRole) && !expectedRole.contains(userInDB.getUserType())) {
                    System.out.println("\n  [!] Access Denied: Role mismatch!");
                    System.out.println("      Your ID belongs to a [" + userInDB.getUserType() + "], but you selected [" + expectedRole + "].\n");
                    continue; 
                }

                currentUserID = userInDB.getUserID();
                currentUserType = userInDB.getUserType();

                System.out.println("  Login Success!! Welcome, " + userInDB.getName() + "!");
                System.out.println("  Role: " + currentUserType + ". Enjoy exploring our collection.\n");
                return currentUserType;
                
            } else if (choice == 4) {
                System.out.print("  Admin Username: ");
                String u = input.nextLine();
                System.out.print("  Admin Password: ");
                String p = input.nextLine();

                for (Account acc : accounts) {
                    if (acc.getUsername().equals(u) && acc.getPassword().equals(p) && acc.getType().equals("Admin")) {
                        System.out.println("  Admin Login Success!");
                        currentUserID = u;
                        currentUserType = "Admin";
                        return "Admin";
                    }
                }
                System.out.println("  Access Denied: Incorrect Admin credentials.");
            } else {
                System.out.println("  Invalid choice, please try again.");
            }
        }
    }

    private static void loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (Scanner r = new Scanner(file)) {
            while (r.hasNextLine()) {
                String[] d = r.nextLine().split(",");
                if (d.length == 3) accounts.add(new Account(d[0], d[1], d[2]));
            }
        } catch (Exception e) {
            System.out.println("  [Error] Failed to load accounts file: " + e.getMessage());
        }
    }
}