public class AdminLibrary extends BaseUser {
    private int borrowLimit;
    private int currentBorrowedBooks = 0;
    private int loanDuration;
    private boolean isActive = true;
    private double outstandingFines = 0.0;

    public AdminLibrary(String name, String id, String type, String email) {
        super(name, id, type, email);
        
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20;
            this.loanDuration = 30;
        } else if (type.equalsIgnoreCase("Student")) {
            this.borrowLimit = 15;
            this.loanDuration = 14;
        } else {
            this.borrowLimit = 7;
            this.loanDuration = 7;
        }
    }

    public String getAdminName(){
        return super.getName();
    }

    public void setAdminName(String name) {
        super.setName(name);
    }

    public double getOutstandingFines() {
        return outstandingFines;
    }

    public void setOutstandingFines(double amount) {
        this.outstandingFines = amount;
    }

    public void setCurrentBorrowedBooks(int amount) {
        this.currentBorrowedBooks = amount;
    }
    
    public void setUserType(String type) {
        super.setUserType(type); 
        if (type.equalsIgnoreCase("Faculty")) {
            this.borrowLimit = 20; 
            this.loanDuration = 30;
        } else if (type.equalsIgnoreCase("Student")) {
            this.borrowLimit = 15; 
            this.loanDuration = 14;
        } else {
            this.borrowLimit = 7;
            this.loanDuration = 7;
        }
    }


    public String getUserID() {
        return super.getUserID();
    }

    public String getUserType() {
        return super.getUserType();
    }
    
    public String getEmail() {
        return super.getEmail();
    }

    public int getLoanDuration() {
        return loanDuration;
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


    

}

