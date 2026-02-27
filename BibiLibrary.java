import java.util.Scanner;
import java.io.*;

public class BibiLibrary {
    private static Scanner input = new Scanner(System.in);
    private static final String USER_FILE = "users_data.txt";
    private static LibraryManager manager = new LibraryManager();

    public static void main(String[] args) {
        String userType = user.loginProcess(input);
        if (userType == null) return;

        while (true) {
            System.out.println("\n======== Bibi Library (" + userType + ") ======== ");
            

            if (userType.equals("Admin")) {
                System.out.println(" 1. Personal (Admin Only)");
                System.out.println(" 2. Book Management");
                System.out.println(" 3. My Loans & Status");
                System.out.println(" 4. Payment & Reports");
            } else {

                System.out.println(" 1. My Loans & Status");
                System.out.println(" 2. Payment & Reports");
            }
            System.out.println(" 0. Exit ");
            System.out.print("Please select: ");

            if (!input.hasNextInt()) { 
                input.nextLine(); 
                System.out.println("Invalid input! Please enter a number.");
                continue; 
            }
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) System.exit(0);


            if (userType.equals("Admin")) {

                switch (choice) {
                    case 1: Personal(); break;
                    case 2: BookType(); break;
                    case 3: LoansAndStatus(); break;
                    case 4: PaymentAndReports(); break;
                    default: System.out.println("Invalid choice.");
                }
            } else {

                switch (choice) {
                    case 1: LoansAndStatus(); break;
                    case 2: PaymentAndReports(); break;
                    default: System.out.println("Invalid choice.");
                }
            }
        }
    }


    private static void Personal() {
        System.out.println("\n--- User Management ---");
        System.out.println("1. Add User Profile\n2. View Profiles from File\n3. View Profiles in Memory");
        int choice = input.nextInt();
        input.nextLine();
        if (choice == 1) AddingUser();
        else if (choice == 2) viewSavedUsers(); 
        else if (choice == 3) manager.displayAllUsers();
    }

    private static void AddingUser() {
        System.out.print("Full Name: "); String name = input.nextLine();
        System.out.print("User ID: "); String id = input.nextLine();
        System.out.print("Email: "); String email = input.nextLine();
        System.out.print("User Type (Student/Staff): "); String type = input.nextLine();

        AdminLibrary profile = new AdminLibrary(name, id, type, email, 10, 0);
        manager.addUser(profile);

        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE, true))) {
            writer.println("Name: " + name + " | ID: " + id + " | Email: " + email);
            System.out.println("✅ User details archived.");
        } catch (IOException e) { System.out.println("Error saving user."); }
    }

    private static void viewSavedUsers() {
        System.out.println("\n--- Archived Users List (TXT) ---");
        File file = new File(USER_FILE);
        if (!file.exists()) { System.out.println("No records found."); return; }
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) System.out.println(reader.nextLine());
        } catch (Exception e) { }
    }

    private static void BookType() { 
        System.out.println("\n--- Book Type Management ---"); 
    }
    private static void LoansAndStatus() { 
        System.out.println("\n--- Circulation Module ---"); 
    }
    private static void PaymentAndReports() { 
        System.out.println("\n--- Payment and Reports ---"); 
    }
}