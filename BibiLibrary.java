import java.util.Scanner;
import java.io.*;

public class BibiLibrary {
    private static Scanner input = new Scanner(System.in);
    private static LibraryManager manager = new LibraryManager();
    private static final String USER_FILE = "users_data.txt";

    public static void main(String[] args) {

        while (true) {
            String userType = user.loginProcess(input);
            if (userType == null) {
                break; 
            }
            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n" +
                    " ____          __               __           __                                              \r\n" +
                    "/\\  _`\\    __/\\ \\      __     /\\ \\       __/\\ \\                                              \r\n" +
                    "\\ \\ \\L\\ \\ /\\_\\ \\ \\____/\\_\\    \\ \\ \\     /\\_\\ \\ \\____  _ __    __     _ __   __  __    \r\n" +
                    " \\ \\  _ <'\\/\\ \\ \\ '__`\\/\\ \\    \\ \\ \\  __\\/\\ \\ \\ '__`\\/\\`'__\\/'__`\\  /\\`'__\\/\\ \\/\\ \\   \r\n" +
                    "  \\ \\ \\L\\ \\\\ \\ \\ \\ \\L\\ \\ \\ \\    \\ \\ \\L\\ \\\\ \\ \\ \\ \\L\\ \\ \\ \\//\\ \\L\\.\\_\\ \\ \\/ \\ \\ \\_\\ \\  \r\n" +
                    "   \\ \\____/ \\ \\_\\ \\_,__/\\ \\_\\    \\ \\____/ \\ \\_\\ \\_,__/\\ \\_\\\\ \\__/.\\_\\\\ \\_\\  \\/`____ \\ \r\n" +
                    "    \\/___/   \\/_/\\/___/  \\/_/     \\/___/   \\/_/\\/___/  \\/_/ \\/__/\\/_/ \\/_/   `/___/> \\\r\n" +
                    "                                                                                 /\\___/\r\n" +
                    "                                                                                 \\/__/ ");
                
                System.out.println("========================================================================================");
                System.out.println("Current Role: " + userType);
                
                if (userType.equals("Admin")) {
                    System.out.println(" 1. User Management\n 2. Audit Inventory\n 3. Reports");
                } else {
                    System.out.println(" 1. Search Catalog\n 2. My Loans\n 3. Reservation");
                }
                System.out.println(" 0. Logout Back to Main Menu");
                System.out.print("Choice: ");

                if (!input.hasNextInt()) {
                    input.nextLine();
                    continue;
                }
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("Logging out...");
                    loggedIn = false; 
                } else {

                    handleMenu(userType, choice);
                }
            }
        }
    }

    private static void handleMenu(String userType, int choice) {
        if (userType.equals("Admin")) {
            switch (choice) {
                case 1: Personal(); break;
                case 2: manager.displayAllUsers(); break;
                case 3: System.out.println("Generating fine reports..."); break;
                default: System.out.println("Invalid choice.");
            }
        } else {
            switch (choice) {
                case 1: 
                    System.out.print("Search: ");
                    manager.searchBooks(input.nextLine());
                    break;
                case 2: System.out.println("Current status: Clear."); break;
                case 3: System.out.println("Reservation sent."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void Personal() {
        System.out.print("Full Name: "); String name = input.nextLine();
        System.out.print("User ID: "); String id = input.nextLine();
        System.out.print("Email: "); String email = input.nextLine();
        System.out.print("Type (Faculty/Student): "); String type = input.nextLine();

        AdminLibrary profile = new AdminLibrary(name, id, type, email);
        manager.addUser(profile);
    }
}