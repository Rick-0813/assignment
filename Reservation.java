import java.time.LocalDate;

public class Reservation {
    private String reservationID;
    private String userID;
    private String itemID;
    private LocalDate requestDate;
    private boolean isFulfilled;

    public Reservation(String reservationID, String userID, String itemID) {
        this.reservationID = reservationID;
        this.userID = userID;
        this.itemID = itemID;
        this.requestDate = LocalDate.now();
        this.isFulfilled = false;
    }

    public String getReservationID() { return reservationID; }
    public String getUserID() { return userID; }
    public String getItemID() { return itemID; }
    public LocalDate getRequestDate() { return requestDate; }
    public boolean isFulfilled() { return isFulfilled; }

    public void setFulfilled(boolean fulfilled) {
        this.isFulfilled = fulfilled;
    }
}