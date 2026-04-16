import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
 

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
    private static final String LOANS_FILE = "loans_data.txt";
    private static final String RESERVATIONS_FILE = "reservations_data.txt";

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
        loadCirculationData();
    }

    public void borrowItem(String userID, String itemID) {
    AdminLibrary currentUser = getUserByID(userID);
    LibraryItem item = getItemById(itemID);

    if (currentUser == null) {
            System.out.println("  [!] Error: User not found.");
            return;
        }
        if (item == null) {
            System.out.println("  [!] Error: Item ID [" + itemID + "] not found in the catalog.");
            return;
        }

        if (!item.isAvailable()) {
            System.out.println("  [!] Item [" + item.getTitle() + "] is currently out of stock.");
            System.out.println("  [System] Placing a reservation request for User [" + userID + "]...");
            reserveItem(userID, itemID);
            return;
        }

        if (!currentUser.canBorrow()) {
            System.out.println("  [!] Failed to borrow item.");
            System.out.println("  Your current user type is " + currentUser.getUserType() + ", and you can borrow up to " + currentUser.getBorrowLimit() + " books.");
            System.out.println("  You have currently borrowed " + currentUser.getCurrentBorrowedBooks() + " books. Please return some books first.");
            return;
        }

        String loanID = "LN" + String.format("%04d", loanCounter++);
        Loan newLoan = new Loan(loanID, userID, itemID, currentUser.getLoanDuration());
        loanList.add(newLoan);
        
        currentUser.incrementBorrowedBooks(); 
        adjustStock(item, -1);
        updateUser();
        saveCatalogToFile();
        addLog(userID, "BORROW", "Borrowed Item: " + itemID + " | Due: " + newLoan.getDueDate());
        saveUsersData(); 
        saveCirculationData();
        
        System.out.println("  Successfully borrowed item! Also You can still borrow ^v^ " + (currentUser.getBorrowLimit() - currentUser.getCurrentBorrowedBooks()) + " more books.");
        System.out.println("  Loan ID   : " + loanID);
        System.out.println("  Issue Date: " + newLoan.getIssueDate());
        System.out.println("  Due Date  : " + newLoan.getDueDate());
    }

    public void returnItem(String userID, String itemID) {
        Loan activeLoan = null;
        for (Loan l: loanList){
            if (l.getUserID().equals(userID) && l.getItemID().equals(itemID) && !l.isReturned()){
                activeLoan = l;
                break;
            }
        }
        
        if (activeLoan == null) {
            System.out.println("  [!] No active loan found for User [" + userID + "] and Item [" + itemID + "].");
            return;
        }

        LibraryItem item = getItemById(itemID);
        AdminLibrary currentUser = getUserByID(userID);

        activeLoan.returnItem();
        currentUser.decrementBorrowedBooks();
        adjustStock(item, 1);

        System.out.println("  [Success] Item returned on: " + activeLoan.getReturnDate());

        if (activeLoan.isLate()) {
           int daysLate = activeLoan.getDaysLate();
            System.out.println("  [!] Warning: Item is returned " + daysLate + " days late.");
            double fineAmt = fineBalance.processFine(userID, item.getTitle(), daysLate, currentUser.getUserType());
            addLog(userID, "FINE_ISSUED", "Late return: " + itemID + " | " + daysLate + " days late | RM " + String.format("%.2f", fineAmt));
        }

        updateUser();
        saveCatalogToFile();
        addLog(userID, "RETURN", "Returned Item: " + itemID);

        checkReservations(itemID);
        saveCirculationData();
    }  
    
    private void reserveItem(String userID, String itemID) {
        String resID = "RS" + String.format("%04d", reserveCounter++);
        Reservation res = new Reservation(resID, userID, itemID);
        reservationList.add(res);
        addLog(userID, "RESERVE", "Placed reservation for Item: " + itemID);
        saveCirculationData();
        System.out.println("  [Success] Reservation placed! ID: " + resID + ". You will be notified when it is returned.");
    }

    private void checkReservations(String itemID) {
        for (Reservation res : reservationList) {
            if (res.getItemID().equals(itemID) && !res.isFulfilled()) {
                System.out.println("  [System Notification] Item [" + itemID + "] is now back in stock and reserved for User [" + res.getUserID() + "].");
                res.setFulfilled(true);
                break; 
            }
        }
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

                pw.println(u.getName() + "," + u.getUserID() + "," + u.getUserType() + "," + u.getEmail() + "," + u.isActive() + "," + u.getCurrentBorrowedBooks() + "," + currentOwed);
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
            System.out.println("  -------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "GENRE" , "STOCK");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            for (Novel n : novels){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                n.getItemId() , limitString(n.getTitle() ,23) , n.getIsbn() , limitString(n.getAuthor(),17) , limitString(n.getGenre(), 15), n.getStockQuantity());
            }
            System.out.println("  -------------------------------------------------------------------------------------------------");
        }

        if (!mangas.isEmpty()){
            System.out.println("\n  Manga Match  ");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"ILLUSTRATOR" , "VOLUME" , "STOCK");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            for (Manga m : mangas){
                String volstr = "Vols. " + m.getVolumeNumber();
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                m.getItemId() , limitString(m.getTitle() ,23) , m.getIsbn() , limitString(m.getIllustrator(),17) , volstr , m.getStockQuantity());
            }
            System.out.println("  -------------------------------------------------------------------------------------------------");
        }

        if (!storybooks.isEmpty()){
            System.out.println("\n  StoryBook Match ");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TARGET AGE" , "STOCK");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            for (StoryBook sb : storybooks){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sb.getItemId() , limitString(sb.getTitle() ,23) , sb.getIsbn() , limitString(sb.getAuthor(),17) , limitString(sb.getTargetAge(), 15), sb.getStockQuantity());
            }
            System.out.println("  -------------------------------------------------------------------------------------------------");
        }

        if (!selfhelps.isEmpty()){
            System.out.println("\n  Self-Help Match ");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" , "ID" , "TITLE" , "ISBN" ,"AUTHOR" , "TOPIC" , "STOCK");
            System.out.println("  -------------------------------------------------------------------------------------------------");
            for (SelfHelp sh :selfhelps){
                System.out.printf("  | %-5s | %-23s | %-13s | %-17s | %-15s | %-5s | %n" ,
                sh.getItemId() , limitString(sh.getTitle() ,23) , sh.getIsbn() , limitString(sh.getAuthor(),17) , limitString(sh.getTopic(), 15), sh.getStockQuantity());
            }
            System.out.println("  -------------------------------------------------------------------------------------------------");
        }
    }

    public void addUser(AdminLibrary newUser){
        userList.add(newUser);
        System.out.println(" User [" + newUser.getName() + "] added successfully.");
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
                System.out.println("User [" + u.getName() + "] is now " + (u.isActive() ? "ENABLED" : "DISABLED") );
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
            
           System.out.printf(format, status, u.getUserID(), u.getUserType(), u.getName(), u.getEmail(), loansInfo, daysInfo, finesInfo);
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

    public void displayUserLoans(String userID) {
    System.out.println("\n            .===================================================================================.");
    System.out.println("            |                                   M Y   L O A N S                                 |");
    System.out.println("            '==================================================================================='");
    System.out.printf("            | %-10s | %-25s | %-12s | %-12s | %-10s |%n", 
                      "Loan ID", "Book Title", "Issue Date", "Due Date", "Status");
    System.out.println("            |-----------------------------------------------------------------------------------|");

    boolean hasLoans = false;
    for (Loan loan : loanList) {
        if (loan.getUserID().equalsIgnoreCase(userID)) {
            LibraryItem item = getItemById(loan.getItemID());
            String title = (item != null) ? item.getTitle() : "Unknown Item";
        
            if (title.length() > 25) title = title.substring(0, 22) + "...";

            String status = loan.isReturned() ? "Returned" : (loan.isLate() ? "OVERDUE" : "Active");

            System.out.printf("            | %-10s | %-25s | %-12s | %-12s | %-10s |%n",
                              loan.getLoanID(), title, loan.getIssueDate(), loan.getDueDate(), status);
            hasLoans = true;
        }
    }

    if (!hasLoans) {
        System.out.println("            |                      You have no current or past loans.                           |");
    }
    System.out.println("            '==================================================================================='");
}


    public void saveCirculationData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOANS_FILE, false))) {
            for (Loan l : loanList) {
                pw.println(l.toFileString());
            }
        } catch (IOException e) {
            System.out.println("  [System] ERROR saving loans data: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE, false))) {
            for (Reservation r : reservationList) {
                pw.println(r.toFileString());
            }
        } catch (IOException e) {
            System.out.println("  [System] ERROR saving reservations data: " + e.getMessage());
        }
    }

    private void loadCirculationData() {
        File loanFile = new File(LOANS_FILE);
        if (loanFile.exists()) {
            try (Scanner scanner = new Scanner(loanFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length == 7) {
                        Loan loan = new Loan(data[0], data[1], data[2], data[3], data[4], data[5], Boolean.parseBoolean(data[6]));
                        loanList.add(loan); 
                        int idNum = Integer.parseInt(data[0].replace("LN", ""));
                        if (idNum >= loanCounter) loanCounter = idNum + 1;
                    }
                }
            } catch (Exception e) {
                System.out.println("  [System Error] Failed to load loans: " + e.getMessage());
            }
        }

        File resFile = new File(RESERVATIONS_FILE);
        if (resFile.exists()) {
            try (Scanner scanner = new Scanner(resFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length == 5) {
                        Reservation res = new Reservation(data[0], data[1], data[2], data[3], Boolean.parseBoolean(data[4]));
                        reservationList.add(res);
                        int idNum = Integer.parseInt(data[0].replace("RS", ""));
                        if (idNum >= reserveCounter) reserveCounter = idNum + 1;
                    }
                }
            } catch (Exception e) {
                System.out.println("  [System Error] Failed to load reservations: " + e.getMessage());
            }
        }
    }

    public boolean verifyUserBorrowedItem(String userID, String itemQuery) {
        for (Loan loan : loanList) {
            if (loan.getUserID().equalsIgnoreCase(userID)) {
                if(loan.getItemID().equalsIgnoreCase(itemQuery)) {
                    return true;
                }

                LibraryItem item = getItemById(loan.getItemID());
                if (item != null && item.getTitle().equalsIgnoreCase(itemQuery)) {
                    return true;
                }
            }
        }
        return false;
    }
}