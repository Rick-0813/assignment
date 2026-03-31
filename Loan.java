import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private String loanID;
    private String userID;
    private String itemID;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public Loan(String loanID, String userID, String itemID, int loanDurationDays) {
        this.loanID = loanID;
        this.userID = userID;
        this.itemID = itemID;
        this.issueDate = LocalDate.now();
        this.dueDate = this.issueDate.plusDays(loanDurationDays);
        this.isReturned = false;
    }

    public String getLoanID() { return loanID; }
    public String getUserID() { return userID; }
    public String getItemID() { return itemID; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }

    public void returnItem() {
        this.returnDate = LocalDate.now();
        this.isReturned = true;
    }

    public boolean isLate() {
        if (!isReturned) {
            return LocalDate.now().isAfter(dueDate);
        }
        return returnDate.isAfter(dueDate);
    }

    public int getDaysLate() {
        if (!isLate()) return 0;
        
        LocalDate checkDate = isReturned ? returnDate : LocalDate.now();
        return (int) ChronoUnit.DAYS.between(dueDate, checkDate);
    }
}