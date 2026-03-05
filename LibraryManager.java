import java.util.ArrayList;

public class LibraryManager {
    private ArrayList<AdminLibrary> userList = new ArrayList<>();
    private ArrayList<Book> bookCatalog = new ArrayList<>();

    public LibraryManager() {
        bookCatalog.add(new Book("Rubik's Cube", "Liang", "", "Education"));
        bookCatalog.add(new Book("Design", "Yong", "ISBN002", "Education"));
    }

    public void searchBooks(String query){
        System.out.println("\n--- Category Search Results ---");
        boolean found = false;
        for (Book b : bookCatalog) {
            if (b.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                b.getIsbn().equals(query)) {
                System.out.println(b);
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
}