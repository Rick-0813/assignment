public class main {
    public static void main(String[] args){

        String mockUserType = "  Student"; 

        System.out.println("  --- System Preview for " + mockUserType + " ---");

        if (mockUserType.equals("  Admin")) {
            System.out.println("  1. User Management\n  2. Book Management\n  3. Audit Inventory");
        } else {
            System.out.println("  1. Search Catalog (Available/On Loan)\n  2. My Loans & Status\n  3. Reserve a Book");
        }
        
        System.out.println("\n  Tip: Please run BibiLibrary.java to start the full system.");
    }
}