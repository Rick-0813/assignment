import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String timestamp;
    private String userID;
    private String action; 
    private String details;

    public Transaction(String userID, String action, String details) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.userID = userID;
        this.action = action;
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "[" + timestamp + "] User: " + userID + " | Action: " + action + " | Details: " + details;
    }
}
