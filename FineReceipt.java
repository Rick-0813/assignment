import java.time.LocalDate;

public class FineReceipt {

    private static int receiptCounter = 1;

    public void printReceipt(String userID, String userName,
        double totalPaid, FineBalance fineBalance) {
        String today = LocalDate.now().toString();
        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID) && r.isPaid()) {
                int overdueDays = r.getFineType().equals("LOST") ?
                    30 :
                    (int) Math.round(r.getOverdueAmount() / 0.50);
                printSingleReceipt(receiptCounter++, r.getFineID(), userID, userName,
                    r.getItemTitle(), overdueDays, r.getTotal(), true, today);
            }
        }
    }

    public static void printSingleReceipt(int receiptNo, String fineID, String userId,
        String userName, String bookTitle,
        int overdueDays, double amount,
        boolean isPaid, String timestamp) {
        String dots = "············";
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   LIBRARY FINE RECEIPT              No. %04d  |%n", receiptNo);
        System.out.println("        |_________________________________________________|");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Fine ID     " + dots + "  %-26s|%n", fineID);
        System.out.printf("        |   Date        " + dots + "  %-26s|%n", timestamp);
        System.out.printf("        |   User ID     " + dots + "  %-26s|%n", userId);
        System.out.printf("        |   Name        " + dots + "  %-26s|%n", userName);
        System.out.printf("        |   Book        " + dots + "  %-26s|%n", bookTitle);
        System.out.printf("        |   Overdue     " + dots + "  %-23s|%n", overdueDays + " days");
        System.out.println("        |                                                 |");
        System.out.println("        |- - - - - - - - - - - - - - - - - - - - - - - - |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Fine Amount              RM         %6.2f   |%n", amount);
        System.out.println("        |                                                 |");
        System.out.println("        |- - - - - - - - - - - - - - - - - - - - - - - - |");
        System.out.println("        |                                                 |");
        if (isPaid) {
            System.out.printf("        |   Status      [ PAID ]     %-21s|%n", timestamp);
            System.out.println("        |                                                 |");
            System.out.println("        |      Thank you. Please return books on time.    |");
        } else {
            System.out.println("        |   Status      [ UNPAID ]   Payment pending      |");
            System.out.println("        |                                                 |");
            System.out.println("        |      Please settle at the counter. Thank you.   |");
        }
        System.out.println("        |_________________________________________________|");
        System.out.println("        /\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/");
    }
}