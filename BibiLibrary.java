import java.util.Scanner;

public class BibiLibrary {
    private static Scanner input = new Scanner(System.in);
    private static LibraryManager manager = new LibraryManager();
    String currentUserID = "";
    public static void main(String[] args) {

        while (true) {
            String userType = StaticUser.loginProcess(input);
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

                System.out.println("=============================================================================\n");
                System.out.println("               .==================================================.");
                System.out.println("              /                                                  /|");
                System.out.printf("             /            Current Role: %-24s/ |%n", userType);
                System.out.println("            /                                                  /  |");
                System.out.println("           .==================================================.   |");

                if (userType.trim().equalsIgnoreCase("Admin")) {
                    System.out.println("           |                                                  |   |");
                    System.out.println("           |    [ 1 ] User Management                         |   |\n           |    [ 2 ] Circulation Module                      |   |\n           |    [ 3 ] Cataloging Admin                        |   |\n           |    [ 4 ] Fees and Audits                         |   .");
                } else {
                    System.out.println("           |                                                  |   |");
                    System.out.println("           |    [ 1 ] Search Catalog                          |   |\n           |    [ 2 ] My Loans                                |   |\n           |    [ 3 ] My Bills                                |   .");
                }
                System.out.println("           |                                                  |  /");
                System.out.println("           |--------------------------------------------------| /");
                System.out.println("           |    [ 0 ] Logout Back to Main Menu                |/");
                System.out.println("           .==================================================.");
                System.out.print("  Choice: ");

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
                case 2:
                    System.out.println("  Managing circulation...");
                    break;
                case 3:
                    catalogMenu();
                    break;
                case 4:
                    feesAndAudits();
                    break;
                default:
                    System.out.println("  Invalid choice.");
            }
        } else {
            switch (choice) {
                case 1:
                    System.out.println("\n            .--------------------------------------------------.");
                    System.out.println("            |             S E A R C H   C A T A L O G          |");
                    System.out.println("            '--------------------------------------------------'");
                    System.out.println("            [Tip] You can search by the book ID or keyword of the book title ");
                    System.out.print("  Enter the keyword :");
                    manager.searchBooks(input.nextLine());
                    break;
                case 2:
                    System.out.println("  Enter your User ID to view loans: ");
                    String userID = input.nextLine();
                    System.out.println("  Checking loans for User ID [" + userID + "]... Current status: Clear.");
                    break;
                case 3:
                    System.out.print("  Enter your User ID to check bills: ");
                    String billUserID = input.nextLine().trim();
                    AdminLibrary billUser = manager.getUserByID(billUserID);
                    String billName = (billUser != null) ? billUser.getAdminName() : "Unknown";
                    manager.getFineMenu().showFineMenuIfOwed(billUserID, billName, input);
                    break;
                default:
                    System.out.println("  Invalid choice.");
            }
        }
    }

    private static void ManagmentUser() {
        while (true) {
            System.out.println("\n             .==================================================.");
            System.out.println("             |   U s e r   M a n a g e m e n t   O p t i o n s  |");
            System.out.println("             ====================================================");
            System.out.println("             |      [ 1 ] Add User                              |");
            System.out.println("             |      [ 2 ] Remove User                           |");
            System.out.println("             |      [ 3 ] Edit User                             |");
            System.out.println("             |      [ 4 ] View All Users                        |");
            System.out.println("             |      [ 5 ] Account Status Control                |");
            System.out.println("             ----------------------------------------------------");
            System.out.println("             |      [ 0 ] Back to Admin Menu                    |");
            System.out.println("             .==================================================.");
            System.out.print("  Select an option: ");

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
                editUser();
            } else if (userChoice == 4) {
                manager.displayAllUsers();
            } else if (userChoice == 5) {
                accountStatusControl();
            } else {
                System.out.println("  Invalid choice. Please try again.");
            }
        }
    }

    private static void editUser() {
        System.out.print("  Enter User ID to edit: ");
        String id = input.nextLine().trim();

        AdminLibrary user = manager.getUserByID(id);

        if (user == null) {
            System.out.println("  [!] User ID not found.");
            return;
        }

        System.out.println("\n                --- Editing User: " + user.getAdminName() + " ---");
        System.out.println("  (Tip: Press ENTER to keep current data)");

        System.out.print("  New Name [" + user.getAdminName() + "]: ");
        String newName = input.nextLine().trim();
        if (!newName.isEmpty()) {
            user.setAdminName(newName);
        }

        System.out.print("  New Email [" + user.getEmail() + "]: ");
        String newEmail = input.nextLine().trim();
        if (!newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }

        System.out.print("  New Type [" + user.getUserType() + "]: ");
        String newType = input.nextLine().trim();
        if (!newType.isEmpty()) {
            user.setUserType(newType);
        }

        manager.updateUser();
        manager.addLog("Admin", "EDIT_USER", "Edited details for User ID: " + id);
        System.out.println("  [System] User details updated successfully!");
    }

    private static void addUser() {
        String name;
        do {
            System.out.print("  Name (IC/Passport): ");
            name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("                   [!] Error: Name cannot be empty. Please try again.");
            }
        } while (name.isEmpty());

        String id;
        do {
            System.out.print("  User ID: ");
            id = input.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("                   [!] Error: User ID cannot be empty. Please try again.");
            }
        } while (id.isEmpty());

        String email;
        do {
            System.out.print("  Email: ");
            email = input.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("                   [!] Error: Email cannot be empty. Please try again.");
            }
        } while (email.isEmpty());

        String type;
        do {

            System.out.print("  Type (Admin/Faculty/Student/Public Member): ");
            type = input.nextLine().trim();
            if (type.isEmpty()) {
                System.out.println("                   [!] Error: Type cannot be empty. Please try again.");
            }
        } while (type.isEmpty());

        AdminLibrary profile = new AdminLibrary(name, id, type, email);

        manager.addUser(profile);
    }

    private static void removeUser() {
        System.out.println("  Enter User ID to remove: ");
        String id = input.nextLine();
        if (manager.removeUser(id)) {
            System.out.println("                   [!] User removed successfully.");
        } else {
            System.out.println("                   [!] User not found.");
        }
    }

    private static void accountStatusControl() {
        System.out.println("  Enter User ID to toggle status ");
        System.out.println("V");
        String id = input.nextLine();
        manager.toggleUserStatus(id);
    }

    private static void catalogMenu() {
        while (true) {
            System.out.println("\n             .==================================================.");
            System.out.println("             |             C A T A L O G   M E N U              |");
            System.out.println("             |==================================================|");
            System.out.println("             |                                                  |");
            System.out.println("             |    [ 1 ]  Add New Item                           |");
            System.out.println("             |    [ 2 ]  View All Catalog                       |");
            System.out.println("             |    [ 3 ]  Update Stock                           |");
            System.out.println("             |    [ 4 ]  Remove Item                            |");
            System.out.println("             |                                                  |");
            System.out.println("             |--------------------------------------------------|");
            System.out.println("             |    [ 0 ]  Back to Admin Menu                     |");
            System.out.println("             '=================================================='");
            System.out.print("  Select an option: ");

            if (!input.hasNextInt()) {
                System.out.println("  Error: Please enter a number.");
                input.nextLine();
                continue;
            }

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) {
                System.out.println("  Returning to Admin Menu...");
                break;
            } else if (choice == 1) {
                addNewCatalogItem();
            } else if (choice == 2) {
                manager.displayAllCatalog();
            } else if (choice == 3) {
                updateCatologItem();
            } else if (choice == 4) {
                System.out.println("\n  .--------------------------------------------------.");
                System.out.println("  |              R E M O V E   I T E M               |");
                System.out.println("  '--------------------------------------------------'");
                System.out.print("  Enter the Item ID you want to remove (e.g. B001) :");
                String removeId = input.nextLine();

                manager.removeCatalogItem(removeId);
            } else {
                System.out.println("  Error: Function " + choice + " is under construction!");
            }
        }
    }

    private static void addNewCatalogItem() {
        System.out.println("\n             .==================================================.");
        System.out.println("             |         S E L E C T   I T E M   T Y P E          |");
        System.out.println("             |==================================================|");
        System.out.println("             |                                                  |");
        System.out.println("             |    [ 1 ]  Novel        (Sci-Fi, Romance...)      |");
        System.out.println("             |    [ 2 ]  Manga        (Comics, Graphic Novels)  |");
        System.out.println("             |    [ 3 ]  StoryBook    (Kids, Picture Books)     |");
        System.out.println("             |    [ 4 ]  Self-Help    (Biography, Finance)      |");
        System.out.println("             |                                                  |");
        System.out.println("             |--------------------------------------------------|");
        System.out.println("             |    [ 0 ]  Cancel & Go Back                       |");
        System.out.println("             '=================================================='");
        System.out.print("  Select item type (0-4): ");

        if (!input.hasNextInt()) {
            System.out.println("  Error! Invalid Input. Please enter number only .");
            input.nextLine();
            return;
        }

        int typeChoice = input.nextInt();
        input.nextLine();

        if (typeChoice == 0) {
            System.out.println("  Action canceled. Returning to Catalog Menu...");
            return;
        }

        if (typeChoice < 1 || typeChoice > 4) {
            System.out.println("  Error! Invalid Input.");
            System.out.println("  Please enter the number between 1 to 4.");
            return;
        }

        System.out.println("\n  .--------------------------------------------------.");
        System.out.println("  |           E N T E R   B O O K   D A T A          |");
        System.out.println("  '--------------------------------------------------'");
        System.out.print("  Enter Item ID            : ");
        String itemId = input.nextLine();
        if (manager.isItemExists(itemId)) {
            System.out.println("  [!] Error . The ID is already exits ! Please use a different ID");
            return;

        }

        System.out.print("  Enter Title              : ");
        String title = input.nextLine();

        System.out.print("  Enter ISBN               : ");
        String isbn = input.nextLine();

        System.out.print("  Enter Stock Quantity     : ");
        if (!input.hasNextInt()) {
            System.out.println("  Error ! Stock must be a number.");
            input.nextLine();
            return;
        }
        int stockQuantity = input.nextInt();
        input.nextLine();

        switch (typeChoice) {
            case 1:
                {
                    System.out.print("  Enter Author             : ");
                    String author = input.nextLine();
                    System.out.print("  Enter Genre              : ");
                    String genre = input.nextLine();

                    Novel newNovel = new Novel(itemId, title, isbn, author, genre, stockQuantity);
                    manager.addCatalogItem(newNovel);
                    break;
                }

            case 2:
                {
                    System.out.print("  Enter Illustrator        : ");
                    String illustrator = input.nextLine();
                    System.out.print("  Enter Volume Number      : ");

                    if (!input.hasNextInt()) {
                        System.out.println("  Error! Issue number must be a number.");
                        input.nextLine();
                        return;
                    }
                    int volumeNumber = input.nextInt();
                    input.nextLine();

                    Manga newManga = new Manga(itemId, title, isbn, illustrator, volumeNumber, stockQuantity);
                    manager.addCatalogItem(newManga);
                    break;
                }

            case 3:
                {
                    System.out.print("  Enter Author             : ");
                    String author = input.nextLine();
                    System.out.print("  Enter Target Age         : ");
                    String targetAge = input.nextLine();

                    StoryBook newStoryBook = new StoryBook(itemId, title, isbn, author, targetAge, stockQuantity);
                    manager.addCatalogItem(newStoryBook);
                    break;
                }
            case 4:
                {
                    System.out.print("  Enter Author             : ");
                    String author = input.nextLine();
                    System.out.print("  Enter Topic              : ");
                    String topic = input.nextLine();

                    SelfHelp newSelfHelp = new SelfHelp(itemId, title, isbn, author, topic, stockQuantity);
                    manager.addCatalogItem(newSelfHelp);
                    break;
                }
        }
    }

    private static void updateCatologItem() {
        System.out.println("\n  .--------------------------------------------------.");
        System.out.println("  |             U P D A T E   S T O C K              |");
        System.out.println("  '--------------------------------------------------'");
        System.out.print("  Enter the Item ID to update :");
        String id = input.nextLine().trim();

        LibraryItem itemToUpdate = manager.getItemById(id);

        if (itemToUpdate == null) {
            System.out.println("  [!] Error! The item is not found in the catalog");
            return;
        }

        int currentStock = 0;
        if (itemToUpdate instanceof Novel) {
            currentStock = ((Novel) itemToUpdate).getStockQuantity();
        } else if (itemToUpdate instanceof Manga) {
            currentStock = ((Manga) itemToUpdate).getStockQuantity();
        } else if (itemToUpdate instanceof StoryBook) {
            currentStock = ((StoryBook) itemToUpdate).getStockQuantity();
        } else if (itemToUpdate instanceof SelfHelp) {
            currentStock = ((SelfHelp) itemToUpdate).getStockQuantity();
        }

        System.out.println("  The current stock of this book is " + currentStock);
        System.out.print("  Enter the New Stock Amount : ");
        if (!input.hasNextInt()) {
            System.out.println("  [!] Error. PLease Enter a valid number");
            input.nextLine();
            return;
        }

        int newStock = input.nextInt();
        input.nextLine();

        if (newStock < 0) {
            System.out.println("  [!] Error. Stock cannot be negative");
            return;
        }

        if (itemToUpdate instanceof Novel) {
            ((Novel) itemToUpdate).setStockQuantity(newStock);
        } else if (itemToUpdate instanceof Manga) {
            ((Manga) itemToUpdate).setStockQuantity(newStock);
        } else if (itemToUpdate instanceof StoryBook) {
            ((StoryBook) itemToUpdate).setStockQuantity(newStock);
        } else if (itemToUpdate instanceof SelfHelp) {
            ((SelfHelp) itemToUpdate).setStockQuantity(newStock);
        }

        System.out.println("  System Success !  Stock for " + itemToUpdate.getTitle() + " updated to " + newStock);
        manager.saveCatalogToFile();
        manager.addLog("Admin", "UPDATE_STOCK", "Updated Item ID [" + id + "] to stock: " + newStock);
    }

    private static void feesAndAudits() {
        Status auditLog = new Status();
        FineReport fineReport = new FineReport(manager.getFineBalance(), manager);

        while (true) {
            System.out.println("\n        _________________________________________________");
            System.out.println("        |                                                 |");
            System.out.println("        |        FEES & AUDITS                            |");
            System.out.println("        |        Library Admin Panel                      |");
            System.out.println("        |_________________________________________________|");
            System.out.println("        |                                                 |");
            System.out.println("        |  -- Fine Management --------------------------  |");
            System.out.println("        |                                                 |");
            System.out.println("        |    [ 1 ]  Manually Add Fine                     |");
            System.out.println("        |    [ 2 ]  View All Fines                        |");
            System.out.println("        |    [ 3 ]  View Fines by User ID                 |");
            System.out.println("        |    [ 4 ]  View Pending Fines (Sorted)           |");
            System.out.println("        |    [ 5 ]  Delete a Fine Entry                   |");
            System.out.println("        |    [ 6 ]  Fines Summary Report                  |");
            System.out.println("        |                                                 |");
            System.out.println("        |  -- Audit Log --------------------------------  |");
            System.out.println("        |                                                 |");
            System.out.println("        |    [ 7 ]  View Full Audit Log                   |");
            System.out.println("        |    [ 8 ]  Search Audit by User ID               |");
            System.out.println("        |    [ 9 ]  Search Audit by Action Type           |");
            System.out.println("        |                                                 |");
            System.out.println("        |- - - - - - - - - - - - - - - - - - - - - - - - |");
            System.out.println("        |    [ 0 ]  Back to Admin Menu                    |");
            System.out.println("        |_________________________________________________|");
            System.out.print("\n  Select an option: ");

            if (!input.hasNextInt()) {
                input.nextLine();
                System.out.println("  Enter a number.");
                continue;
            }
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) {
                System.out.println("  Returning to Admin Menu...");
                break;
            } else if (choice == 1) {
                manuallyAddFine();
            } else if (choice == 2) {
                fineReport.displayAllFinesWithNames();
            } else if (choice == 3) {
                System.out.print("  Enter User ID: ");
                String uid = input.nextLine().trim();
                AdminLibrary u = manager.getUserByID(uid);
                String name = (u != null) ? u.getAdminName() : "Unknown";
                manager.getFineMenu().showFineDetails(uid, name);
            } else if (choice == 4) {
                fineReport.displayPendingFinesSorted();
            } else if (choice == 5) {
                System.out.print("  Enter User ID to delete fine from: ");
                String uid = input.nextLine().trim();
                AdminLibrary u = manager.getUserByID(uid);
                String name = (u != null) ? u.getAdminName() : "Unknown";
                manager.getFineMenu().deleteFineEntry(uid, name, input);
                manager.addLog("Admin", "CORRECTION", "Deleted fine for User [" + uid + "]");
            } else if (choice == 6) {
                fineReport.generateSummaryReport();
            } else if (choice == 7) {
                auditLog.displayFullAuditLog();
            } else if (choice == 8) {
                System.out.print("  Enter User ID to search: ");
                auditLog.searchAuditByUserID(input.nextLine().trim());
            } else if (choice == 9) {
                auditLog.searchAuditByAction(input);
            } else {
                System.out.println("  Invalid choice.");
            }
        }
    }

    private static void manuallyAddFine() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        MANUALLY ADD FINE                        |");
        System.out.println("        |        Admin Fine Entry                         |");
        System.out.println("        |_________________________________________________|");

        System.out.print("\n  Enter User ID    : ");
        String userID = input.nextLine().trim();
        if (userID.isEmpty()) {
            System.out.println("  User ID cannot be empty.");
            return;
        }

        AdminLibrary u = manager.getUserByID(userID);
        if (u == null) {
            System.out.println("  !! User ID not found.");
            return;
        }

        System.out.print("  Enter Item Title : ");
        String itemTitle = input.nextLine().trim();
        if (itemTitle.isEmpty()) {
            System.out.println("  Item title cannot be empty.");
            return;
        }

        System.out.print("  Enter Days Late  : ");
        if (!input.hasNextInt()) {
            input.nextLine();
            System.out.println("  Enter a number.");
            return;
        }
        int daysLate = input.nextInt();
        input.nextLine();
        if (daysLate <= 0) {
            System.out.println("  Must be more than 0.");
            return;
        }

        double charged = manager.getFineBalance().processFine(userID, itemTitle, daysLate);
        manager.addLog("Admin", "MANUAL_FINE",
            "Fine for [" + userID + "] " + u.getAdminName() +
            " | Item: " + itemTitle +
            " | Days: " + daysLate +
            " | RM: " + String.format("%.2f", charged));
        System.out.printf("  RM %.2f recorded for [%s - %s].%n", charged, userID, u.getAdminName());
    }

}