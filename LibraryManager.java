import java.util.ArrayList;

public class LibraryManager {
    private ArrayList<AdminLibrary> userList = new ArrayList<>();
    private ArrayList<LibraryItem> bookCatalog = new ArrayList<>();

    public LibraryManager() {
        bookCatalog.add(new Book("B001", "Java Programming", "Chong", "ISBN001",5));
        bookCatalog.add(new Magazine("M001", "Tech Monthly", "TechPress", 42 , 10));
        bookCatalog.add(new DVD("D001","Inception","Christopher Nolan", 148, 3));
    }

    public void addCatalogItem(LibraryItem item){
        bookCatalog.add(item);
        System.out.println("Success! [ "+ item.getTitle() +" ] is added to the catalog.");
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
    }

    public boolean removeUser(String id) {
        return userList.removeIf(u -> u.getUserID().equals(id));
    }
    
    public void toggleUserStatus(String id) {
        for (AdminLibrary u : userList) {
            if (u.getUserID().equals(id)) {
                u.setActive(!u.isActive());
                System.out.println("User [" + u.getAdminName() + "] is now " + (u.isActive() ? "ENABLED" : "DISABLED") );
                return;
            }
        }
        System.out.println(" Error: User ID [" + id + "] is not found.");
    }

    public void displayAllUsers() {
        System.out.println("\n--- Registered Patrons ---");
        for (AdminLibrary u : userList) {
            String status = u.isActive() ? "[ACTIVE]" : "[DISABLED]";
            System.out.println(status + " ID: " + u.getUserID() + " | Name: " + u.getAdminName());
        }
    }

    public void displayAllcatalog(){
        System.out.println("\n --- Complete Library Catalog --- ");

        if (bookCatalog.isEmpty()){
            System.out.println("The catalog is currently empty.");
            return;
        }

        for (LibraryItem item : bookCatalog){
            item.displayItemDetails();
        }
    }
}