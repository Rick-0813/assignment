public class AdminLibrary {
    private String AdminName;
    private String UserID;
    private String UserType;
    private String Email;;
    private int BorrowLimit; 
    private int CurrentBorrowedBooks;

    //Parameterized Constructor
    public AdminLibrary(String AdminName, String UserID, String UserType, String Email, int BorrowLimit, int CurrentBorrowedBooks) {
        this.AdminName = AdminName;
        this.UserID = UserID;
        this.UserType = UserType;
        this.Email = Email;
        this.BorrowLimit = BorrowLimit;
        this.CurrentBorrowedBooks = CurrentBorrowedBooks;
    }
}
