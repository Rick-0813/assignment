public class AdminLibrary {
    private String adminName;
    private String userID;
    private String userType;
    private String email;
    private int borrowLimit;
    private int currentBorrowedBooks = 0;
    private int loanDuration;
    private boolean isActive = true;
    private double outstandingFines = 0.0;

    public double getOutstandingFines() { 
        return outstandingFines; 
    }
     
    public void setOutstandingFines(double amount) {
        this.outstandingFines = amount;
    }

    public void setCurrentBorrowedBooks(int amount) {
        this.currentBorrowedBooks = amount;
    }

    public void setAdminName(String name) {
        this.adminName = name;
    }

    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public void setUserType(String type) { 
        this.userType = type; 
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20; 
            this.loanDuration = 30; 
        } else if (type.equalsIgnoreCase("Student")) {
            this.borrowLimit = 10; 
            this.loanDuration = 14;  
        } else { 
            this.borrowLimit = 5; 
            this.loanDuration = 7;  
        }
    }
    
    public int getLoanDuration() { 
        return loanDuration; 
    }

    public AdminLibrary(String name, String id, String type, String email) {
        this.adminName = name;
        this.userID = id;
        this.userType = type; 
        this.email = email;
        
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20; 
            this.loanDuration = 30; 
        } else if (type.equalsIgnoreCase("Student")) {
            this.borrowLimit = 15; 
            this.loanDuration = 14;  
        } else { 
            this.borrowLimit = 7; 
            this.loanDuration = 8;  
        }
    }

    public boolean canBorrow() {
        return currentBorrowedBooks < borrowLimit;
    }

    public void incrementBorrowedBooks() {
        if (canBorrow()) {
            currentBorrowedBooks++;
        }
    }

    public void decrementBorrowedBooks() {
        if (currentBorrowedBooks > 0) {
            currentBorrowedBooks--;
        }
    }

    public int getBorrowLimit() { 
        return borrowLimit; 
    }
    public int getCurrentBorrowedBooks() { 
        return currentBorrowedBooks; 
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

    public String getUserType() { 
        return userType; 
    }
    
    public String getEmail() { 
        return email; 
    }
}

