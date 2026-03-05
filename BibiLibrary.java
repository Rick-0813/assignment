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
                System.out.println(",-----.  ,--.,--.   ,--.   ,--.   ,--.,--.                                  \r\n" + //
                                        "|  |) /_ `--'|  |-. `--'   |  |   `--'|  |-. ,--.--. ,--,--.,--.--.,--. ,--.\r\n" + //
                                        "|  .-.  \\,--.| .-. ',--.   |  |   ,--.| .-. '|  .--'' ,-.  ||  .--' \\  '  / \r\n" + //
                                        "|  '--' /|  || `-' ||  |   |  '--.|  || `-' ||  |   \\ '-'  ||  |     \\   '  \r\n" + //
                                        "`------' `--' `---' `--'   `-----'`--' `---' `--'    `--`--'`--'   .-'  /   \r\n" + //
                                        "                                                                   `---'    ");
                
                System.out.println("=============================================================================");
                System.out.println("Current Role: " + userType);
                
                if (userType.equals("Admin")) {
                    System.out.println(" 1. User Management\n 2. Circulation Module\n 3. Cataloging Admin\n 4. Fees and Audits");
                } else {
                    System.out.println(" 1. Search Catalog\n 2. My Loans\n 3. My Bills");
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
                case 1: ManagmentUser(); break;
                case 2: System.out.println("Managing circulation..."); break;
                case 3: System.out.println("Generating fine reports..."); break;
                case 4: System.out.println("Performing audits..."); break;
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

    private static void ManagmentUser() {
    while (true) {
        System.out.println("============================");
        System.out.println("| User Management Options  |");
        System.out.println("============================");
        System.out.println("|      1. Add User         |");
        System.out.println("|     2. Remove User       |");
        System.out.println("|    3. View All Users     |");
        System.out.println("| 4. Account Status Control|");
        System.out.println("|    0. Back to Admin Menu |");
        System.out.println("===========================");
        System.out.print("Select an option: ");

        if (!input.hasNextInt()) {
            System.out.println("Error: Please enter a number.");
            input.nextLine(); 
            continue; 
        }

        int userChoice = input.nextInt();
        input.nextLine(); 
        //if userchoice is 0, break the loop and return to admin menu
        if (userChoice == 0) {
            System.out.println("Returning to Admin Menu...");
            break;
        } else if (userChoice == 1) {
            addUser();
        } else if (userChoice == 2) {
            removeUser();
        } else if (userChoice == 3) {
            manager.displayAllUsers();
        } else if (userChoice == 4) {
            accountStatusControl();
        } else {
            System.out.println("Invalid choice. Please try again.");
        }

    }
}

    
    private static void addUser() {
        System.out.println("Name (IC/Passport): ");
        String name = input.nextLine();
        System.out.println("User ID: ");
        String id = input.nextLine();
        System.out.println("Email: ");
        String email = input.nextLine();
        System.out.println("Type (Admin/Patron): ");
        String type = input.nextLine();
        
        AdminLibrary profile = new AdminLibrary(name, id, type, email);
        manager.addUser(profile);
    }

    private static void removeUser() {
        System.out.println("Enter User ID to remove: ");
        String id = input.nextLine();
        if (manager.removeUser(id)) {
            System.out.println("✅ User removed successfully.");
        } else {
            System.out.println("❌ User not found.");
        }
    }   
    
    private static void accountStatusControl() {
        System.out.println("Enter User ID to toggle status: ");
        String id = input.nextLine();
        manager.toggleUserStatus(id);
    }
} 