import java.time.LocalDate;

public class FineReceipt {

    private static int receiptCounter = 1;

    public void printReceipt(String userID, String userName,
        double totalPaid, FineBalance fineBalance) {
        String today = LocalDate.now().toString();
        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID) && r.isPaid()) {
                int days = r.getFineType().equals("LOST") ?
                    30 : (int) Math.round(r.getOverdueAmount() / 0.50);
                printSingleReceipt(receiptCounter++, userID, userName,
                    r.getItemTitle(), days, r.getTotal(), true, today);
            }
        }
    }

    public static void printSingleReceipt(int receiptNo, String userId, String userName,
        String bookTitle, int overdueDays,
        double amount, boolean isPaid, String timestamp) {
        String dots = "············";
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   LIBRARY FINE RECEIPT              No. %04d  |%n", receiptNo);
        System.out.println("        |_________________________________________________|");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Date        %s  %-26s|%n", dots, timestamp);
        System.out.printf("        |   User ID     %s  %-26s|%n", dots, userId);
        System.out.printf("        |   Name        %s  %-26s|%n", dots, userName);
        System.out.printf("        |   Book        %s  %-26s|%n", dots, bookTitle);
        System.out.printf("        |   Overdue     %s  %-23s|%n", dots, overdueDays + " days");
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