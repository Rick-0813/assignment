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
            System.out.println("\n        _________________________________________________");
            System.out.println("        |                                                 |");
            System.out.println("        |        MY FINE & BILLS                          |");
            System.out.println("        |_________________________________________________|");
            System.out.println("        |                                                 |");
            System.out.printf("        |   User ID  : %-35s|%n", userID);
            System.out.printf("        |   Name     : %-35s|%n", userName);
            System.out.println("        |                                                 |");
            System.out.println("        |   Your fine balance is clear.                   |");
            System.out.println("        |   No outstanding fines. Keep it up!  :)         |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        while (true) {
            System.out.println("\n        _________________________________________________");
            System.out.println("        |                                                 |");
            System.out.println("        |        MY FINE & BILLS                          |");
            System.out.println("        |_________________________________________________|");
            System.out.println("        |                                                 |");
            System.out.printf("        |   User ID     : %-31s|%n", userID);
            System.out.printf("        |   Name        : %-31s|%n", userName);
            System.out.printf("        |   Outstanding : RM %-28.2f|%n",
                fineBalance.getOutstandingBalance(userID));
            System.out.println("        |                                                 |");
            System.out.println("        |    [ 1 ]  View Fine Details                     |");
            System.out.println("        |    [ 2 ]  Pay All Fines                         |");
            System.out.println("        |                                                 |");
            System.out.println("        |- - - - - - - - - - - - - - - - - - - - - - - - |");
            System.out.println("        |    [ 0 ]  Back                                  |");
            System.out.println("        |_________________________________________________|");
            System.out.print("\n  Select an option: ");

            if (!input.hasNextInt()) {
                input.nextLine();
                System.out.println("  Enter a number.");
                continue;
            }
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) break;
            else if (choice == 1) showFineDetails(userID, userName);
            else if (choice == 2) processFinePayment(userID, userName);
            else System.out.println("  Invalid choice.");
        }
    }

    public void showFineDetails(String userID, String userName) {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        FINE DETAILS                             |");
        System.out.println("        |_________________________________________________|");
        System.out.println("        |                                                 |");
        System.out.printf("        |   User ID  : %-35s|%n", userID);
        System.out.printf("        |   Name     : %-35s|%n", userName);
        System.out.println("        |_________________________________________________|");
        System.out.printf("%n  %-6s  %-20s  %-7s  %-10s  %-10s  %-10s  %-8s%n",
            "ID", "Item Title", "Type", "Date", "Overdue RM", "Penalty RM", "Status");
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────");
        boolean found = false;
        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                System.out.printf("  %-6s  %-20s  %-7s  %-10s  %-10.2f  %-10.2f  %-8s%n",
                    r.getFineID(), r.getItemTitle(), r.getFineType(), r.getDate(),
                    r.getOverdueAmount(), r.getLostPenalty(),
                    r.isPaid() ? "PAID" : "UNPAID");
                found = true;
            }
        }

        if (!found) System.out.println("  No fine records found.");
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────");
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
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        DELETE FINE ENTRY                        |");
        System.out.println("        |_________________________________________________|");
        System.out.println("        |                                                 |");
        System.out.printf("        |   User ID : %-35s|%n", userID);
        System.out.printf("        |   Name    : %-35s|%n", userName);
        System.out.println("        |_________________________________________________|");
        boolean found = false;
        System.out.printf("%n  %-6s  %-20s  %-7s  %-10s  %-8s  %-8s%n",
            "ID", "Item Title", "Type", "Date", "Total RM", "Status");
        System.out.println("  ────────────────────────────────────────────────────────────────");

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                System.out.printf("  %-6s  %-20s  %-7s  %-10s  %-8.2f  %-8s%n",
                    r.getFineID(), r.getItemTitle(), r.getFineType(),
                    r.getDate(), r.getTotal(), r.isPaid() ? "PAID" : "UNPAID");
                found = true;
            }
        }

        if (!found) {
            System.out.println("  No fine records found for this user.");
            return;
        }

        System.out.println("  ────────────────────────────────────────────────────────────────");
        System.out.print("  Enter Fine ID to delete, or 0 to cancel: ");
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