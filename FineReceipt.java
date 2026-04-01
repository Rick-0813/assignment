import java.time.LocalDate;

public class FineReceipt {

    public void printReceipt(String userID, String userName,
        double amountPaid, FineBalance fineBalance) {

        System.out.println("\n             .==================================================.");
        System.out.println("             | B I B I  L I B R A R Y - F I N E  R E C E I P T  |");
        System.out.println("             |==================================================|");
        System.out.printf("             |  User ID  : %-38s|%n", userID);
        System.out.printf("             |  Name     : %-38s|%n", userName);
        System.out.printf("             |  Date     : %-38s|%n", LocalDate.now().toString());
        System.out.println("             |--------------------------------------------------|");
        System.out.println("             |  Items Paid:                                     |");

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID) && r.isPaid()) {
                System.out.printf("             |    %-20s [%-7s] RM %-9.2f|%n",
                    r.getItemTitle(), r.getFineType(), r.getTotal());
            }
        }

        System.out.println("             |--------------------------------------------------|");
        System.out.printf("             |    Total Paid       : RM %-24.2f|%n", amountPaid);
        System.out.printf("             |    Remaining Balance: RM %-24.2f|%n",
            fineBalance.getOutstandingBalance(userID));
        System.out.println("             |==================================================|");
        System.out.println("             |       Thank you for your payment! ^-^            |");
        System.out.println("             .==================================================.\n");
    }
}