public class AdminLibrary {
    private String name;
    private String UserID;
    private String UserType;
    private String contactNumber;
    private int BorrowLimit;
    private int CurrentBorrowedBooks;

    //Parameterized Constructor
    public AdminLibrary(String name, String UserID, String UserType, String contactNumber, int BorrowLimit, int CurrentBorrowedBooks) {
        this.name = name;
        this.UserID = UserID;
        this.UserType = UserType;
        this.contactNumber = contactNumber;
        this.BorrowLimit = BorrowLimit;
        this.CurrentBorrowedBooks = CurrentBorrowedBooks;
    }
}
