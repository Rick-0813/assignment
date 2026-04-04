import java.util.Scanner;

public class FineMenu {
    private FineBalance fineBalance;
    private FineReceipt receipt;
    // Constructor
    public FineMenu(FineBalance fineBalance) {
        this.fineBalance = fineBalance;
        this.receipt = new FineReceipt();
    }

    public void showFineMenuIfOwed(String userID, String userName, Scanner input) {
        if (!fineBalance.hasPendingFines(userID)) {
            System.out.println("\n             .==================================================.");
            System.out.println("             |                                                  |");
            System.out.println("             |            M Y  F I N E  &  B I L L S            |");
            System.out.println("             |__________________________________________________|");
            System.out.println("             |                                                  |");
            System.out.printf("             |   User ID  : %-36s|%n", userID);
            System.out.printf("             |   Name     : %-36s|%n", userName);
            System.out.println("             |                                                  |");
            System.out.println("             |   Your fine balance is clear.                    |");
            System.out.println("             |   No outstanding fines. Keep it up!  :)          |");
            System.out.println("             '=================================================='");
            return;
        }

        boolean isViewBills = true;
        while (isViewBills) {
            System.out.println("\n             .==================================================.");
            System.out.println("             |                                                  |");
            System.out.println("             |            M Y  F I N E  &  B I L L S            |");
            System.out.println("             |__________________________________________________|");
            System.out.println("             |                                                  |");
            System.out.printf("             |   User ID     : %-33s|%n", userID);
            System.out.printf("             |   Name        : %-33s|%n", userName);
            System.out.printf("             |   Outstanding : RM %-30.2f|%n",
                fineBalance.getOutstandingBalance(userID));
            System.out.println("             |                                                  |");
            System.out.println("             |    [ 1 ]  View Fine Details                      |");
            System.out.println("             |    [ 2 ]  Pay All Fines                          |");
            System.out.println("             |                                                  |");
            System.out.println("             |- - - - - - - - - - - - - - - - - - - - - - - - - |");
            System.out.println("             |    [ 0 ]  Back                                   |");
            System.out.println("             |__________________________________________________|");
            System.out.print("\n  Select an option: ");

            if (!input.hasNextInt()) {
                input.nextLine();
                System.out.println("  Enter a number.");
                continue;
            }
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) {
                isViewBills = false;
            } else if (choice == 1) showFineDetails(userID, userName);
            else if (choice == 2) processFinePayment(userID, userName);
            else System.out.println("  Invalid choice.");
        }
    }

    public void showFineDetails(String userID, String userName) {
        System.out.println("\n                      .==================================================.");
        System.out.println("                      |                                                  |");
        System.out.println("                      |              F I N E  D E T A I L S              |");
        System.out.println("                      |__________________________________________________|");
        System.out.println("                      |                                                  |");
        System.out.printf("                      |       User ID  : %-32s|%n", userID);
        System.out.printf("                      |       Name     : %-32s|%n", userName);
        System.out.println("                      '=================================================='");
        System.out.println("  .============================================================================================.");
        System.out.printf("  | %-5s | %-20s | %-8s | %-10s | %-11s | %-11s | %-8s|%n",
            "ID", "Item Title", "Type", "Date", "Overdue RM", "Penalty RM", "Status");
        System.out.println("  |────────────────────────────────────────────────────────────────────────────────────────────|");
        boolean found = false;
        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                System.out.printf("  | %-5s | %-20s | %-8s | %-10s | %-11.2f | %-11.2f | %-8s|%n",
                    r.getFineID(), r.getItemTitle(), r.getFineType(), r.getDate(),
                    r.getOverdueAmount(), r.getLostPenalty(),
                    r.isPaid() ? "PAID" : "UNPAID");
                found = true;
            }
        }

        if (!found) {
            System.out.printf("  | %-90s |%n", "No fine records found.");
        }
        System.out.println("  '============================================================================================'");
        System.out.printf("  Outstanding Balance : RM %.2f%n", fineBalance.getOutstandingBalance(userID));
    }

    private void processFinePayment(String userID, String userName) {
        double amount = fineBalance.getOutstandingBalance(userID);
        if (fineBalance.payFine(userID)) {
            receipt.printReceipt(userID, userName, amount, fineBalance);
        } else {
            System.out.println("  No outstanding fines to pay.");
        }
    }

    public void deleteFineEntry(String userID, String userName, Scanner input) {
        System.out.println("\n             .==================================================.");
        System.out.println("             |                                                  |");
        System.out.println("             |          D E L E T E  F I N E  E N T R Y         |");
        System.out.println("             |__________________________________________________|");
        System.out.println("             |                                                  |");
        System.out.printf("             |       User ID : %-33s|%n", userID);
        System.out.printf("             |       Name    : %-33s|%n", userName);
        System.out.println("             '=================================================='\n");
        boolean found = false;
        System.out.println("  .=============================================================================.");
        System.out.printf ("  | %-6s | %-20s | %-8s | %-10s | %-8s | %-8s |%n",
            "ID", "Item Title", "Type", "Date", "Total RM", "Status");
        System.out.println("  |─────────────────────────────────────────────────────────────────────────────|");

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
            String safeTitle = r.getItemTitle().length() > 20 ? r.getItemTitle().substring(0, 17) + "..." : r.getItemTitle();
                
                System.out.printf("  | %-6s | %-20s | %-8s | %-10s | %-8.2f | %-8s |%n",
                    r.getFineID(), safeTitle, r.getFineType(),
                    r.getDate(), r.getTotal(), r.isPaid() ? "PAID" : "UNPAID");
                found = true;
            }
        }

        if (!found) {
            System.out.printf ("  | %-75s |%n", "No fine records found for this user.");
            System.out.println("  '============================================================================='");
            return;
        }

        System.out.println("  '============================================================================='");
        
        System.out.print("\n  Enter Fine ID to delete, or 0 to cancel: ");
        String fineID = input.nextLine().trim();

        if (fineID.equals("0")) {
            System.out.println("  Cancelled.");
            return;
        }

        FineBalance.FineRecord toDelete = fineBalance.getFineByID(fineID);
        if (toDelete == null) {
            System.out.println("  !! Fine ID not found.");
            return;
        }
        if (!toDelete.getUserID().equals(userID)) {
            System.out.println("  !! Fine ID does not belong to this user.");
            return;
        }

        System.out.printf("  Confirm delete: %s  %s  RM %.2f  (y/n): ",
            toDelete.getFineID(), toDelete.getItemTitle(), toDelete.getTotal());
        String confirm = input.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            fineBalance.deleteFineByID(fineID);
            System.out.println("  Fine " + fineID + " deleted.");
        } else {
            System.out.println("  Cancelled.");
        }
    }
}