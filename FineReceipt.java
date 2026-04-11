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

        String safeTitle = bookTitle.length() > 42 ? bookTitle.substring(0, 39) + "..." : bookTitle;
        String amountStr = String.format("RM %.2f", amount);

        System.out.println("\n  .================================================================.");
        System.out.println("  |                                                                |");
        System.out.println("  |               L I B R A R Y   R E C E I P T                    |");
        System.out.printf ("  |                                                    No. %04d    |%n", receiptNo);
        System.out.println("  |----------------------------------------------------------------|");
        System.out.println("  |                                                                |");
        System.out.printf ("  |   %-13s : %-44s |%n", "Fine ID", fineID);
        System.out.printf ("  |   %-13s : %-44s |%n", "Date", timestamp);
        System.out.printf ("  |   %-13s : %-44s |%n", "User ID", userId);
        System.out.printf ("  |   %-13s : %-44s |%n", "Name", userName);
        System.out.printf ("  |   %-13s : %-44s |%n", "Book", safeTitle);
        System.out.printf ("  |   %-13s : %-44s |%n", "Overdue", overdueDays + " days");
        System.out.println("  |                                                                |");
        System.out.println("  |----------------------------------------------------------------|");
        System.out.println("  |                                                                |");
        System.out.printf ("  |   %-13s : %-44s |%n", "Fine Amount", amountStr);
        System.out.println("  |                                                                |");
        System.out.println("  | - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  |");
        System.out.println("  |                                                                |");
        
        if (isPaid) {
            System.out.printf ("  |   %-13s : %-44s |%n", "Status", "[ PAID ]");
            System.out.println("  |                                                                |");
            System.out.println("  |         Thank you. Please return books on time.                |");
        } else {
            System.out.printf ("  |   %-13s : %-44s |%n", "Status", "[ UNPAID ]  Payment pending");
            System.out.println("  |                                                                |");
            System.out.println("  |         Please settle at the counter. Thank you.               |");
        }
        
        System.out.println("  |                                                                |");
        System.out.println("  '================================================================'");
    }
}