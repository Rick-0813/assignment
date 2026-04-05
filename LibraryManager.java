import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LibraryManager {

    private ArrayList<Loan> loanList = new ArrayList<>();
    private ArrayList<Reservation> reservationList = new ArrayList<>();
    private int loanCounter = 1;
    private int reserveCounter = 1;

    private ArrayList<AdminLibrary> userList = new ArrayList<>();
    private ArrayList<LibraryItem> bookCatalog = new ArrayList<>();
    private FineBalance fineBalance = new FineBalance();
    private FineMenu fineMenu = new FineMenu(fineBalance);
    
    private static final String LOG_FILE = "system_logs.txt";
    private static final String USER_DATA_FILE = "users_data.txt";

    public FineMenu getFineMenu() {
        return fineMenu;
    }
    
    public FineBalance getFineBalance() {
        return fineBalance;
    }

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
    AdminLibrary currentUser = getUserByID(userID);

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
                    try {
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
                    } catch (NumberFormatException e) {
                        System.out.println("  [!!System Warning] Data format error, skipping line: " + line);
                    }
                }
            }
            System.out.println("  [System] Status: Success! Loaded " + count + " user(s) into memory.");
        } catch (Exception e) {
            System.out.println("  [System Error] ERROR loading data: " + e.getMessage());
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
        Transaction newLog = new Transaction(userID, action, details);
        
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
            PrintWriter pw = new PrintWriter(fw)) {    
            pw.println(newLog.toString());
        } catch (IOException e) { 
            System.out.println("  [System Error] Failed to write log.");
        }
    }

    public void addCatalogItem(LibraryItem item){
        bookCatalog.add(item);
        System.out.println("  Success! ["+ item.getTitle() +"] is added to the catalog.");
        addLog("Admin", "ADD_CATALOG", "Added Item: " + item.getItemId());
        saveCatalogToFile();
    }

    public boolean isItemExists(String itemId){
        for (LibraryItem item : bookCatalog) {
            if (item.getItemId().equalsIgnoreCase(itemId)){
                return true;
            }
        }
        return false;
    }

    public LibraryItem getItemById(String Id) {
        for (LibraryItem item : bookCatalog) {
            if (item.getItemId().equalsIgnoreCase(Id)){
                return item;
            }
        }
        return null;
    }

    public void searchBooks(String query){
        String lowerQuery = query.trim().toLowerCase(); 
        boolean found = false;

        if (lowerQuery.isEmpty()) {
            System.out.println("\n  [!] Error: Search keyword cannot be empty.");
            return;
        }

        ArrayList<Novel> novels = new ArrayList<>();
        ArrayList<Manga> mangas = new ArrayList<>();
        ArrayList<StoryBook> storybooks = new ArrayList<>();
        ArrayList<SelfHelp> selfhelps = new ArrayList<>();

        for (LibraryItem item : bookCatalog){
            if (item.getTitle().toLowerCase().contains(lowerQuery) || item.getItemId().toLowerCase().contains(lowerQuery)){
                found = true ;
                if (item instanceof Novel)
                    novels.add((Novel) item);
                else if (item instanceof Manga)
                    mangas.add((Manga) item);
                else if (item instanceof StoryBook)
                    storybooks.add((StoryBook) item);
                else if (item instanceof SelfHelp)
                    selfhelps.add((SelfHelp) item);
            }
        }

        if (!found) {
            System.out.println("\n  [!] No matching books found for query: '" + query + "'");
            return;
        }

        System.out.println("\n  =================================================================================================");
        System.out.println("  |                                  S E A R C H   R E S U L T S                                  |");
        System.out.println("  =================================================================================================");
        
        if (!novels.isEmpty()){
            System.out.println("\n  Novel Match ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "GENRE" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (Novel n : novels){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                n.getItemId() , limitString(n.getTitle() ,23) , n.getIsbn() , limitString(n.getAuthor(),17) , limitString(n.getGenre(), 15), n.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!mangas.isEmpty()){
            System.out.println("\n  Manga Match  ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"ILLUSTRATOR" , "VOLUME" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (Manga m : mangas){
                String volstr = "Vols. " + m.getVolumeNumber();
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                m.getItemId() , limitString(m.getTitle() ,23) , m.getIsbn() , limitString(m.getIllustrator(),17) , volstr , m.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!storybooks.isEmpty()){
            System.out.println("\n  StoryBook Match ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TARGET AGE" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (StoryBook sb : storybooks){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sb.getItemId() , limitString(sb.getTitle() ,23) , sb.getIsbn() , limitString(sb.getAuthor(),17) , limitString(sb.getTargetAge(), 15), sb.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!selfhelps.isEmpty()){
            System.out.println("\n  Self-Help Match ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TOPIC" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (SelfHelp sh :selfhelps){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sh.getItemId() , limitString(sh.getTitle() ,23) , sh.getIsbn() , limitString(sh.getAuthor(),17) , limitString(sh.getTopic(), 15), sh.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }
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
        
        String format = "| %-10s | %-10s | %-12s | %-18s | %-30s | %-11s | %-10s | %-18s|%n";
        
        System.out.println(".=============================================================================================================================================.");
        
        System.out.printf(format, " STATUS", "USER ID", "TYPE", "NAME", "EMAIL", "LOANS (QTY)", "MAX DAYS", "OUTSTANDING FINES ");
        System.out.println(".=============================================================================================================================================.");
        
        for (AdminLibrary u : userList) {
            String status = u.isActive() ? "[ACTIVE]" : "[DISABLED]";
            double owed = fineBalance.getOutstandingBalance(u.getUserID());
            
            String loansInfo = u.getCurrentBorrowedBooks() + " / " + u.getBorrowLimit();
            
            String daysInfo = u.getLoanDuration() + " Days";
            
            String finesInfo = String.format("RM %.2f", owed); 
            
            System.out.printf(format, status, u.getUserID(), u.getUserType(), u.getAdminName(), u.getEmail(), loansInfo, daysInfo, finesInfo);
        }
        System.out.println("'============================================================================================================================================='\n");
    }

    public void displayAllCatalog(){
        System.out.println("\n  ==================================================================================================");
        System.out.println("  |                                           C A T A L O G                                        |");
        System.out.println("  ==================================================================================================");

        if (bookCatalog.isEmpty()){
            System.out.println("| The catalog is currently empty.                                                                |");
            System.out.println("  ==================================================================================================");
        }

        ArrayList<Novel> novels = new ArrayList<>();
        ArrayList<Manga> mangas = new ArrayList<>();
        ArrayList<StoryBook> storybooks = new ArrayList<>();
        ArrayList<SelfHelp> selfhelps = new ArrayList<>();

        for (LibraryItem item : bookCatalog){
            if (item instanceof Novel)
                novels.add((Novel) item);
            else if (item instanceof Manga)
                mangas.add((Manga) item);
            else if (item instanceof StoryBook)
                storybooks.add((StoryBook) item);
            else if (item instanceof SelfHelp)
                selfhelps.add((SelfHelp) item);
        }

        if (!novels.isEmpty()){
            System.out.println("\n  1.Novel ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "GENRE" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (Novel n : novels){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                n.getItemId() , limitString(n.getTitle() ,23) , n.getIsbn() , limitString(n.getAuthor(),17) , limitString(n.getGenre(), 15), n.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!mangas.isEmpty()){
            System.out.println("\n  2.Manga  ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"ILLUSTRATOR" , "VOLUME" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (Manga m : mangas){
                String volstr = "Vols. " + m.getVolumeNumber();
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                m.getItemId() , limitString(m.getTitle() ,23) , m.getIsbn() , limitString(m.getIllustrator(),17) , volstr , m.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!storybooks.isEmpty()){
            System.out.println("\n  3.StoryBook ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TARGET AGE" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (StoryBook sb : storybooks){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sb.getItemId() , limitString(sb.getTitle() ,23) , sb.getIsbn() , limitString(sb.getAuthor(),17) , limitString(sb.getTargetAge(), 15), sb.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
        }

        if (!selfhelps.isEmpty()){
            System.out.println("\n  4.Self Help ");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TOPIC" , "STOCK");
            System.out.println("  --------------------------------------------------------------------------------------------------");
            for (SelfHelp sh :selfhelps){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sh.getItemId() , limitString(sh.getTitle() ,23) , sh.getIsbn() , limitString(sh.getAuthor(),17) , limitString(sh.getTopic(), 15), sh.getStockQuantity());
            }
            System.out.println("  --------------------------------------------------------------------------------------------------");
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
        if(!file.exists()) return;

        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] data = line.split("\\s*,\\s*");

                if (data.length >= 7) {
                    try {
                        String type = data[0];
                        String itemId = data[1];
                        String title = data[2];
                        String isbn = data[3];

                        if (type.equalsIgnoreCase("Novel")){
                            bookCatalog.add(new Novel(itemId, title, isbn, data[4], data[5], Integer.parseInt(data[6])));
                        }
                        else if (type.equalsIgnoreCase("Manga")) {
                            bookCatalog.add(new Manga(itemId, title, isbn, data[4], Integer.parseInt(data[5]), Integer.parseInt(data[6])));
                        }
                        else if (type.equalsIgnoreCase("StoryBook")) {
                            bookCatalog.add(new StoryBook(itemId, title, isbn, data[4], data[5], Integer.parseInt(data[6])));
                        }
                        else if (type.equalsIgnoreCase("SelfHelp")){
                            bookCatalog.add(new SelfHelp(itemId, title, isbn, data[4], data[5], Integer.parseInt(data[6])));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  [!!System Warning] Data format error, skipping line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  [System Error] System error loading catalog data: " + e.getMessage());
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

    private void adjustStock(LibraryItem item, int amount) {
        if (item instanceof Novel) {
            ((Novel)item).setStockQuantity(((Novel)item).getStockQuantity() + amount);
        } else if (item instanceof Manga) {
            ((Manga)item).setStockQuantity(((Manga)item).getStockQuantity() + amount);
        } else if (item instanceof StoryBook) {
            ((StoryBook)item).setStockQuantity(((StoryBook)item).getStockQuantity() + amount);
        } else if (item instanceof SelfHelp) {
            ((SelfHelp)item).setStockQuantity(((SelfHelp)item).getStockQuantity() + amount);
        }
    }

    private String limitString(String text , int maxLength) {
        if (text == null){
            return "" ;
        }
        if (text.length() > maxLength){
            return text.substring( 0 , maxLength-3) + "...";
        }
        return text;
    }
}