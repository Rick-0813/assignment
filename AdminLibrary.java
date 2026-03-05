public class AdminLibrary {
    private String adminName;
    private String userID;
    private String userType;
    private String email;
    private int borrowLimit;
    private int currentBorrowedBooks;
    private int loanDuration;
    private boolean isActive = true;

    public AdminLibrary(String name, String id, String type, String email) {
        this.adminName = name;
        this.userID = id;
        this.userType = type; 
        this.email = email;
        
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20; 
            this.loanDuration = 30; 
        } else {
            this.borrowLimit = 5; 
            this.loanDuration = 14;  
        }
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
    public String getAdminName() { 
        return adminName; 
    }
    public String getUserID() { 
        return userID; 
    }
}