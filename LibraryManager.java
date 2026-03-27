import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LibraryManager {
    private ArrayList<AdminLibrary> userList = new ArrayList<>();
    private ArrayList<LibraryItem> bookCatalog = new ArrayList<>();
    private FineBalance fineBalance = new FineBalance();
    private FineMenu fineMenu = new FineMenu(fineBalance);
    
    private static final String LOG_FILE = "system_logs.txt";
    private static final String USER_DATA_FILE = "users_data.txt";

    public FineMenu getFineMenu() { return fineMenu; }

    public LibraryManager() {
        File catFile = new File("catalog_data.txt");
        if(catFile.exists() && catFile.length() > 0){
            loadCatalogFromFile();
        }
        else {
bookCatalog.add(new Novel("N001", "The Great Gatsby", "978074", "F. Scott", "Fiction", 5));
            bookCatalog.add(new Manga("M001", "Naruto", "978156", "Kishimoto", 1, 10));
            bookCatalog.add(new StoryBook("S001", "Peppa Pig", "978024", "Neville", "3-5 years", 3));
            bookCatalog.add(new SelfHelp("H001", "Atomic Habits", "978073", "James Clear", "Self Improvement", 7));
        }
        
        System.out.println("\n  [System Boot] Booting up Library Database...");
        loadUsersData();
    }

    public void borrowItem(String userID, String itemID) {
    AdminLibrary currentUser = null;

    for (AdminLibrary u : userList) {
        if (u.getUserID().equals(userID)) {
            currentUser = u;
            break;
        }
    }

    if (currentUser == null) {
        System.out.println("  [!] Error: User not found.");
        return;
    }

    if (!currentUser.canBorrow()) {
        System.out.println("  [!] Failed to borrow item.");
        System.out.println("  Your current user type is " + currentUser.getUserType() + ", and you can borrow up to " + currentUser.getBorrowLimit() + " books.");
        System.out.println("  You have currently borrowed " + currentUser.getCurrentBorrowedBooks() + " books. Please return some books first.");
        return;
    }
    
    currentUser.incrementBorrowedBooks(); 
    saveUsersData(); 
    
    System.out.println("  Successfully borrowed item! Also You can still borrow ^v^ " + (currentUser.getBorrowLimit() - currentUser.getCurrentBorrowedBooks()) + " more books.");
}

    public AdminLibrary getUserByID(String id) {
        for (AdminLibrary u : userList) {
            if (u.getUserID().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    public void updateUser() {
        saveUsersData();
    }

    private void loadUsersData() {
        File file = new File(USER_DATA_FILE);
        System.out.println("  [System] Looking for data at: " + file.getAbsolutePath());
        
        if (!file.exists()) {
            System.out.println("  [System] Status: No previous data found. Starting fresh.");
            return;
        }
        
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] data = line.split(",");
                if (data.length >= 5) {
                    AdminLibrary loadedUser = new AdminLibrary(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim());
                    loadedUser.setActive(Boolean.parseBoolean(data[4].trim()));
                    
                    if (data.length >= 6) {
                        loadedUser.setCurrentBorrowedBooks(Integer.parseInt(data[5].trim()));
                    }
                    
                    if (data.length >= 7) {
                        loadedUser.setOutstandingFines(Double.parseDouble(data[6].trim()));
                    }
                    userList.add(loadedUser);
                    count++;
                }
            }
            System.out.println("  [System] Status: Success! Loaded " + count + " user(s) into memory.");
        } catch (Exception e) {
            System.out.println("  [System] ERROR loading data: " + e.getMessage());
        }
    }

    private void saveUsersData() {
        File file = new File(USER_DATA_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (AdminLibrary u : userList) {
                double currentOwed = fineBalance.getOutstandingBalance(u.getUserID());
                u.setOutstandingFines(currentOwed);

                pw.println(u.getAdminName() + "," + u.getUserID() + "," + u.getUserType() + "," + u.getEmail() + "," + u.isActive() + "," + u.getCurrentBorrowedBooks() + "," + currentOwed);
            }
            pw.flush(); 
            System.out.println("  [System] Save trigger: Data successfully updated at " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("  [System] ERROR saving data: " + e.getMessage());
        }
    }

    public void addLog(String userID, String action, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = "[" + timestamp + "] User: " + userID + " | Action: " + action + " | Details: " + details;
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
            PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logEntry);
        } catch (IOException e) { }
    }

    public void addCatalogItem(LibraryItem item){
        bookCatalog.add(item);
        System.out.println("  Success! [ "+ item.getTitle() +" ] is added to the catalog.");
        addLog("Admin", "ADD_CATALOG", "Added Item: " + item.getItemId());
        saveCatalogToFile();
    }

    public void searchBooks(String query){
        System.out.println("\n  --- Category Search Results ---");
        boolean found = false;
        for (LibraryItem item : bookCatalog) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                item.getItemId().equals(query)) {
                System.out.println(item);
                found = true;
            }
        }
        if (!found) System.out.println(" No matching books found for query: " + query);
    }

    public void addUser(AdminLibrary newUser){
        userList.add(newUser);
        System.out.println(" User [" + newUser.getAdminName() + "] added successfully.");
        addLog("Admin", "ADD_USER", "Added User ID: " + newUser.getUserID());
        saveUsersData();
    }

    public boolean removeUser(String id) {
        boolean removed = userList.removeIf(u -> u.getUserID().equals(id));
        if (removed) {
            System.out.println(" User removed successfully.");
            addLog("Admin", "REMOVE_USER", "Removed User ID: " + id);
            saveUsersData(); 
        }
        return removed;
    }
    
    public void toggleUserStatus(String id) {
        for (AdminLibrary u : userList) {
            if (u.getUserID().equals(id)) {
                u.setActive(!u.isActive());
                System.out.println("User [" + u.getAdminName() + "] is now " + (u.isActive() ? "ENABLED" : "DISABLED") );
                addLog("Admin", "TOGGLE_STATUS", "Changed status of User ID: " + id);
                saveUsersData();
                return;
            }
        }
        System.out.println(" Error: User ID [" + id + "] is not found.");
    }

    public void displayAllUsers() { 
        System.out.println("\n                                                       --- Registered Patrons ---");
        if (userList.isEmpty()) {
            System.out.println("  [!] No users currently exist in the system.");
            return;
        }
        
        String format = " %-10s | %-10s | %-12s | %-18s | %-25s | %-11s | %-10s | %s%n";
        
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        
        System.out.printf(format, "STATUS", "USER ID", "TYPE", "NAME", "EMAIL", "LOANS (QTY)", "MAX DAYS", "OUTSTANDING FINES");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        
        for (AdminLibrary u : userList) {
            String status = u.isActive() ? "[ACTIVE]" : "[DISABLED]";
            double owed = fineBalance.getOutstandingBalance(u.getUserID());
            
            String loansInfo = u.getCurrentBorrowedBooks() + " / " + u.getBorrowLimit();
            
            String daysInfo = u.getLoanDuration() + " Days";
            
            String finesInfo = String.format("RM %.2f", owed); 
            
            System.out.printf(format, status, u.getUserID(), u.getUserType(), u.getAdminName(), u.getEmail(), loansInfo, daysInfo, finesInfo);
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void displayAllCatalog(){
        System.out.println("\n   --- Complete Library Catalog --- ");
        if (bookCatalog.isEmpty()){
            System.out.println("  The catalog is currently empty.");
            return;
        }
        for (LibraryItem item : bookCatalog){
            item.displayItemDetails();
        }
    }

    public void saveCatalogToFile() {

        try (PrintWriter writer = new PrintWriter (new FileWriter ("catalog_data.txt"))) {
            
            for(LibraryItem item : bookCatalog){
                writer.println(item.toFileString());
            }

            System.out.println("  Data successfully saved to catalog_data.txt");
        }

        catch(Exception e){
            System.out.println("  Error , data not save to catalog_data,txt") ;
        }
    }

    private void loadCatalogFromFile(){
        File file = new File ("catalog_data.txt");
        if(!file.exists())
            return;

        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.isEmpty())
                    continue;

                String[] data = line.split("\\s*,\\s*");    //using . to slpit and remove the the space before and after the ,

                if (data.length >= 7) {
                    String type = data[0];
                    String itemId = data[1];
                    String title = data[2];
                    String isbn = data[3];

                if (type.equalsIgnoreCase("Novel")){
                    bookCatalog.add( new StoryBook (itemId, title ,isbn , data[4], data[5], Integer.parseInt(data[6])));
                }
                else if (type.equals("Manga")) {
                    bookCatalog.add(new Manga(itemId, title ,isbn , data[4], Integer.parseInt(data[5]), Integer.parseInt(data[6])));
                }
                else if (type.equals("StoryBook")) {
                    bookCatalog.add(new StoryBook(itemId, title ,isbn, data[4], data[5], Integer.parseInt(data[6])));
                }
                else if (type.equalsIgnoreCase("SelfHelp")){
                    bookCatalog.add(new SelfHelp(itemId, title ,isbn , data[4], data[5], Integer.parseInt(data[6])));
                }
            }
            }
            }
        catch (Exception e) {
            System.out.println("System error to loading catalog data");
        }
    }

    public void removeCatalogItem(String itemId){

        boolean isRemoved = bookCatalog.removeIf(item -> item.getItemId().equalsIgnoreCase(itemId));

        if(isRemoved){
            System.out.println("  Success ! Item  [" + itemId + "] has been permanently removed.");
        saveCatalogToFile();
        }

        else {
            System.out.println("  Failed ! Item  [" + itemId + "] not found in the catalog.");
        }

    }
}