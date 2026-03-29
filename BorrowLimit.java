public class BorrowLimit {
    
    public static int getMaxLimit(String userType) {
        if (userType.equalsIgnoreCase("Student")) {
            return 15;
        } else if (userType.equalsIgnoreCase("Faculty")) {
            return 20; 
        } else if (userType.equalsIgnoreCase("Public Member") || userType.equalsIgnoreCase("Member")) {
            return 7;
        }
        return 5; 
    }

    public static boolean canStillBorrow(String userType, int currentBorrowedBooks) {
        int limit = getMaxLimit(userType);
        return currentBorrowedBooks < limit;
    }

    
}