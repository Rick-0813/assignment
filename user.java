import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class user {
    private static ArrayList<Account> accounts = new ArrayList<>();
    private static final String FILE_NAME = "accounts.txt";

    static {
        loadAccounts();
        if (accounts.isEmpty()) {
            accounts.add(new Account("admin", "1234", "Admin"));
        }
    }

    static class Account {
        String username, password, type;
        Account(String u, String p, String t) {
            this.username = u; this.password = p; this.type = t;
        }
    }

    public static String loginProcess(Scanner input) {
        System.out.println("\n--- Welcome to Bibi Library ---");
        System.out.println("1. Login\n2. Register");
        System.out.print("Choice: ");
        
        if (!input.hasNextInt()) { input.nextLine(); return loginProcess(input); }
        int choice = input.nextInt();
        input.nextLine(); 

        if (choice == 2) {
            register(input);
            return loginProcess(input); 
        }

        System.out.print("Username: "); String u = input.nextLine();
        System.out.print("Password: "); String p = input.nextLine();

        for (Account acc : accounts) {
            if (acc.username.equals(u) && acc.password.equals(p)) {
                System.out.println("Login Success! Type: " + acc.type);
                return acc.type; 
            }
        }
        System.out.println("Invalid credentials.");
        return null;
    }

    private static void register(Scanner input) {
        System.out.print("New Username: "); String u = input.nextLine();
        System.out.print("New Password: "); String p = input.nextLine();
        System.out.println("Register as: 1. Student  2. Faculty");
        int t = input.nextInt(); input.nextLine();
        String type = (t == 1) ? "Student" : "Faculty";
        accounts.add(new Account(u, p, type));
        saveAccounts();
        System.out.println("Register successful!");
    }

    private static void saveAccounts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Account acc : accounts) writer.println(acc.username + "," + acc.password + "," + acc.type);
        } catch (IOException e) { }
    }

    private static void loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",");
                if (data.length == 3) accounts.add(new Account(data[0], data[1], data[2]));
            }
        } catch (Exception e) { }
    }
}