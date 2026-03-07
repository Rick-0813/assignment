import java.util.Scanner;

public class BibiLibrary {
    private static Scanner input = new Scanner(System.in);
    private static LibraryManager manager = new LibraryManager();
    private static final String USER_FILE = "users_data.txt";
    String currentUserID = "";
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
                
                if (userType.trim().equalsIgnoreCase("Admin")) {
                    System.out.println("  1. User Management\n  2. Circulation Module\n  3. Cataloging Admin\n  4. Fees and Audits");
                } else {
                    System.out.println("  1. Search Catalog\n  2. My Loans\n  3. My Bills");
                }
                System.out.println("  0. Logout Back to Main Menu");
                System.out.print("Choice: ");

                if (!input.hasNextInt()) {
                    input.nextLine();
                    continue;
                }
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("  Logging out...");
                    loggedIn = false;
                } else {

                    handleMenu(userType, choice);
                }
            }
        }
    }

    private static void handleMenu(String userType, int choice) {
        if (userType.trim().equalsIgnoreCase("Admin")) {
            switch (choice) {
                case 1: 
                ManagmentUser(); 
                break;
                case 2: System.out.println("  Managing circulation..."); break;
                case 3: catalogMenu(); break;
                case 4: System.out.println("  Performing audits..."); break;
                default: System.out.println("  Invalid choice.");
            }
        } else {
            switch (choice) {
                case 1:
                System.out.print("Search: ");
                manager.searchBooks(input.nextLine());
                break;
                case 2: 
                System.out.println("Enter your User ID to view loans: ");
                String userID = input.nextLine(); 
                System.out.println("Checking loans for User ID [" + userID + "]... Current status: Clear.");
                break;
                case 3: 
                System.out.print("Enter your User ID to check bills: ");
                String billUserID = input.nextLine();
                manager.getFineMenu().showFineMenuIfOwed(billUserID, userType.trim(), input);
                break;
                default: 
                System.out.println("  Invalid choice.");
            }
        }
    }

    private static void ManagmentUser() {
    while (true) {
        System.out.println("  ============================");
        System.out.println("  | User Management Options  |");
        System.out.println("  ============================");
        System.out.println("  |      1. Add User         |");
        System.out.println("  |     2. Remove User       |");
        System.out.println("  |    3. View All Users     |");
        System.out.println("  | 4. Account Status Control|");
        System.out.println("  |    0. Back to Admin Menu |");
        System.out.println("  ============================");
        System.out.print("  Select an option: ");

        if (!input.hasNextInt()) {
            System.out.println("  Error: Please enter a number.");
            input.nextLine(); 
            continue; 
        }

        int userChoice = input.nextInt();
        input.nextLine(); 
        if (userChoice == 0) {
            System.out.println("  Returning to Admin Menu...");
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
            System.out.println("  Invalid choice. Please try again.");
        }

    }
}

    
    private static void addUser() {
        String name;
        do {
            System.out.print("  Name (IC/Passport): ");
            name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("  [!] Error: Name cannot be empty. Please try again.");
            }
        } while (name.isEmpty());

        String id;
        do {
            System.out.print("  User ID: ");
            id = input.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("  [!] Error: User ID cannot be empty. Please try again.");
            }
        } while (id.isEmpty());

        String email;
        do {
            System.out.print("  Email: ");
            email = input.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("  [!] Error: Email cannot be empty. Please try again.");
            }
        } while (email.isEmpty());

        String type;
        do {

            System.out.print("  Type (Admin/Faculty/Student/Public Member): ");
            type = input.nextLine().trim();
            if (type.isEmpty()) {
                System.out.println("  [!] Error: Type cannot be empty. Please try again.");
            }
        } while (type.isEmpty());
        
        AdminLibrary profile = new AdminLibrary(name, id, type, email);
        
        manager.addUser(profile); 
    }
    


    private static void removeUser() {
        System.out.println("  Enter User ID to remove: ");
        String id = input.nextLine();
        if (manager.removeUser(id)) {
            System.out.println("  User removed successfully.");
        } else {
            System.out.println("   User not found.");
        }
    }   
    
    private static void accountStatusControl() {
        System.out.println("  Enter User ID to toggle status: ");
        String id = input.nextLine();
        manager.toggleUserStatus(id);
    }


    private static void  catalogMenu() {
        while(true){
            System.out.println("\n  ============================");
            System.out.println("  | Catalog Inventory Menu   |");
            System.out.println("  ============================");
            System.out.println("  |  1. Add New Item         |");
            System.out.println("  |  2. View All Catalog     |");
            System.out.println("  |  3. Update Stock         |");
            System.out.println("  |  4. Remove Item          |");
            System.out.println("  |  0. Back to Admin Menu   |");
            System.out.println("  ============================");
            System.out.print("  Select an option: ");

            if(!input.hasNextInt()){
                System.out.println("  Error: Please enter a number.");
                    input.nextLine();
                    continue;
            }

            int choice = input.nextInt();
            input.nextLine();

            if(choice == 0){
                System.out.println("  Returning to Admin Menu...");
                break;
            }
            else if (choice == 1){
                addNewCatalogItem();
            }

            else if (choice == 2) {
                manager.displayAllcatalog();
            }

            else{
                System.out.println("  Function " + choice + " is under construction!");
            }
        }
    }

        private static void addNewCatalogItem() {
            System.out.println("\n  ============================");
            System.out.println("  | Catalog Inventory Menu   |");
            System.out.println("  ============================");
            System.out.println("  |  1. Add a Book           |");
            System.out.println("  |  2. Add a Magazine       |");
            System.out.println("  |  3. Add a DVD            |");
            System.out.println("  ============================");
            System.out.print("  Select item type (1, 2, or 3): ");

            if(!input.hasNextInt()){
                System.out.println("  Error! Invalid Input. Please enter number only .");
                input.nextLine();
                return;
            }

            int typeChoice = input.nextInt() ;
            input.nextLine() ;

            if (typeChoice < 1 || typeChoice > 3){
                System.out.println("  Error! Invalid Input.");
                return;
            }


            System.out.print("  Enter Item ID (e.g., B001 , M001 , D002) : ");
            String itemId = input.nextLine();
            
            System.out.print("  Enter Title : ");
            String title = input.nextLine();

            System.out.print("  Enter Stock QUantity : ");
            if (!input.hasNextInt()){
                System.out.println("  Error ! Stock must be a number.");
                input.nextLine();
                return;
            }
            int stockQuantity = input.nextInt();
            input.nextLine();

            if(typeChoice == 1) {
                System.out.print("  Enter Author : ");
                String author =  input.nextLine();
                System.out.print("  Enter ISBN : ");
                String isbn = input.nextLine();

                Book newBook = new Book (itemId , title , author , isbn , stockQuantity);
                manager.addCatalogItem(newBook);
            }

            else if (typeChoice == 2){
                System.out.print("  Enter Publisher : ");
                String publisher = input.nextLine();
                System.out.print("  Enter Issue Number : ");

                if(!input.hasNextInt()){
                    System.out.println("  Error! Issue number must be a number.");
                    input.nextLine();
                    return;
                }
                int issueNumber = input.nextInt();
                input.nextLine();

                Magazine newMag = new Magazine (itemId, title , publisher, issueNumber , stockQuantity);
                manager.addCatalogItem(newMag);
            }

            else if ( typeChoice == 3){
                System.out.print("  Enter Director : ");
                String director = input.nextLine();
                System.out.print("  Enter Duration (in minutes) : ");

                if (!input.hasNextInt()){
                    System.out.println("  Error! Duration must be a number");
                    input.nextLine();
                    return;
                }
                int duration = input.nextInt();
                input.nextLine();

                DVD newDvd = new DVD (itemId , title , director , duration , stockQuantity);
                manager.addCatalogItem(newDvd);

            }
        }
}