public class AdminLibrary {
    private String adminName;
    private String userID;
    private String userType;
    private String email;
    private int borrowLimit;
    private int currentBorrowedBooks;
    private int loanDuration;

    public AdminLibrary(String name, String id, String type, String email) {
        this.adminName = name;
        this.userID = id;
        this.userType = type; 
        this.email = email;
        this.currentBorrowedBooks = 0;
        
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20; 
            this.loanDuration = 30; 
        } else {
            this.borrowLimit = 5; 
            this.loanDuration = 14;  
        }
    }


    public String getAdminName() { return adminName; }
    public String getUserID() { return userID; }
    public String getUserType() { return userType; }
    public int getLoanDuration() { return loanDuration; }
}