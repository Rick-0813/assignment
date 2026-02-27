import java.util.ArrayList;

public class LibraryManager {
    private ArrayList<AdminLibrary> userList = new ArrayList<>();
    private ArrayList<Book> bookCatalog = new ArrayList<>();

    public LibraryManager() {

        bookCatalog.add(new Book("Java Basics", "Liang", "ISBN001", "Education"));
        bookCatalog.add(new Book("Database Design", "Bibi", "ISBN002", "Education"));
    }

    public void addUser(AdminLibrary newUser) {
        userList.add(newUser);
        System.out.println(" System: Profile [" + newUser.getAdminName() + "] created.");
    }

    public void displayAllUsers() {
        System.out.println("\n--- Registered Patrons ---");
        for (AdminLibrary u : userList) {
            System.out.println("ID: " + u.getUserID() + " | Name: " + u.getAdminName() + " | Limit: " + u.getLoanDuration() + " days");
        }
    }


    public void searchBooks(String query) {
        System.out.println("\n--- Search Results ---");
        boolean found = false;
        for (Book b : bookCatalog) {
            if (b.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                b.getAuthor().toLowerCase().contains(query.toLowerCase()) || 
                b.getIsbn().equals(query)) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) System.out.println("No matching books found.");
    }
}