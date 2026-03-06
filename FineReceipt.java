public class FineReceipt {

    public void printReceipt(String userID, String userName,
            double amountPaid, FineBalance fineBalance) {
        System.out.println("\n  ============================================");
        System.out.println("  |         BIBI LIBRARY - FINE RECEIPT      |");
        System.out.println("  ============================================");
        System.out.println("  User ID   : " + userID);
        System.out.println("  Name      : " + userName);
        System.out.println("  --------------------------------------------");
        System.out.println("  Items Paid:");

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID) && r.isPaid()) {
                System.out.printf("  - %-25s [%s] RM %.2f%n",
                        r.getItemTitle(), r.getFineType(), r.getTotal());
            }
        }

        System.out.println("  --------------------------------------------");
        System.out.printf("  Total Paid      : RM %.2f%n", amountPaid);
        System.out.printf("  Remaining Balance: RM %.2f%n",
                fineBalance.getOutstandingBalance(userID));
        System.out.println("  ============================================");
        System.out.println("  Thank you for your payment!");
        System.out.println("  ============================================\n");
    }
}