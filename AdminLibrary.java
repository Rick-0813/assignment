public class AdminLibrary {
    private String adminName;
    private String userID;
    private String userType;
    private String email;
    private int borrowLimit; 
    private int currentBorrowedBooks;

    public AdminLibrary(String adminName, String userID, String userType, String email, int borrowLimit, int currentBorrowedBooks) {
        this.adminName = adminName;
        this.userID = userID;
        this.userType = userType;
        this.email = email;
        this.borrowLimit = borrowLimit;
        this.currentBorrowedBooks = currentBorrowedBooks;
    }

   
    public String getUserID() { return userID; }
    public String getAdminName() { return adminName; }
    public String getUserType() { return userType; }
    public String getEmail() { return email; }
}