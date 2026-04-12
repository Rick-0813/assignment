public class FineRate {
    public static double getRatePerDay(String userType) {
        if (userType == null) return 0.50;
        if (userType.equalsIgnoreCase("Faculty"))     return 0.30;
        if (userType.equalsIgnoreCase("Student"))     return 0.50;
        return 0.70; // Public Member
    }
}
 