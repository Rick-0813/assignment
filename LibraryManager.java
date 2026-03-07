import java.util.ArrayList;
import java.io.File;
import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        bookCatalog.add(new Book("B001", "Java Programming", "Chong", "ISBN001",5));
        bookCatalog.add(new Magazine("M001", "Tech Monthly", "TechPress", 42 , 10));
        bookCatalog.add(new DVD("D001","Inception","Christopher Nolan", 148, 3));
        }
        fineBalance.processFine("S001FOIT", "Java Programming", 10); 
        
        System.out.println("\n  [System Boot] Booting up Library Database...");
        loadUsersData(); 
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
                pw.println(u.getAdminName() + "," + u.getUserID() + "," + u.getUserType() + "," + u.getEmail() + "," + u.isActive());
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
        System.out.println("Success! [ "+ item.getTitle() +" ] is added to the catalog.");
        addLog("Admin", "ADD_CATALOG", "Added Item: " + item.getItemId());
        saveCatalogToFile();
    }

    public void searchBooks(String query){
        System.out.println("\n--- Category Search Results ---");
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
        System.out.println("\n--- Registered Patrons ---");
        if (userList.isEmpty()) {
            System.out.println("  [!] No users currently exist in the system.");
        }
        
        System.out.printf("%-12s | %-10s | %-15s | %-20s | %s%n", "STATUS", "USER ID", "TYPE", "NAME", "OUTSTANDING FINES");
        System.out.println("--------------------------------------------------------------------------------------");
        
        for (AdminLibrary u : userList) {
            String status = u.isActive() ? "[ACTIVE]" : "[DISABLED]";
            
            double owed = fineBalance.getOutstandingBalance(u.getUserID());
            
           
            System.out.printf("%-12s | %-10s | %-15s | %-20s | RM %.2f%n", 
                            status, u.getUserID(), u.getUserType(), u.getAdminName(), owed);
        }
        System.out.println("--------------------------------------------------------------------------------------");
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

                String[] data = line.split(",");

                if (data[0].equals("Book") && data.length >= 6){
                    bookCatalog.add( new Book (data[1], data[2], data[3], data[4], Integer.parseInt(data[5])));
                }
                else if (data[0].equals("Magazine") && data.length >= 6) {
                    bookCatalog.add(new Magazine(data[1], data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5])));
                }
                else if (data[0].equals("DVD") && data.length >= 6) {
                    bookCatalog.add(new DVD(data[1], data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5])));
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