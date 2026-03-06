import java.util.Scanner;

public class FineMenu {

    private FineBalance fineBalance;
    private FineReceipt receipt;

    public FineMenu(FineBalance fineBalance) {
        this.fineBalance = fineBalance;
        this.receipt = new FineReceipt();
    }

    public void showFineMenuIfOwed(String userID, String userName, Scanner input) {
        if (!fineBalance.hasPendingFines(userID)) {
            System.out.println("  Your fine balance is clear. No outstanding fines.");
            return;
        }

        while (true) {
            System.out.println("\n  ==============================");
            System.out.println("  |      My Fine & Bills       |");
            System.out.println("  ==============================");
            System.out.printf("  | Total Outstanding: RM %.2f%n",
                    fineBalance.getOutstandingBalance(userID));
            System.out.println("  ==============================");
            System.out.println("  |  1. View Fine Details      |");
            System.out.println("  |  2. Pay All Fines          |");
            System.out.println("  |  0. Back                   |");
            System.out.println("  ==============================");
            System.out.print("  Select an option: ");

            if (!input.hasNextInt()) {
                System.out.println("  Error: Please enter a number.");
                input.nextLine();
                continue;
            }

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                showFineDetails(userID);
            } else if (choice == 2) {
                processFinePayment(userID, userName);
            } else {
                System.out.println("  Invalid choice.");
            }
        }
    }

    private void showFineDetails(String userID) {
        System.out.println("\n  --- Fine Details for User: " + userID + " ---");
        boolean found = false;

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                System.out.printf("  Item    : %s%n", r.getItemTitle());
                System.out.printf("  Type    : %s%n", r.getFineType());
                if (r.getFineType().equals("LOST")) {
                    System.out.printf("  Overdue : RM %.2f%n", r.getOverdueAmount());
                    System.out.printf("  Penalty : RM %.2f%n", r.getLostPenalty());
                }
                System.out.printf("  Total   : RM %.2f%n", r.getTotal());
                System.out.println("  Status  : " + (r.isPaid() ? "PAID" : "UNPAID"));
                System.out.println("  ------------------------------");
                found = true;
            }
        }

        if (!found)
            System.out.println("  No records found.");
        System.out.printf("  Outstanding Balance: RM %.2f%n",
                fineBalance.getOutstandingBalance(userID));
    }

    private void processFinePayment(String userID, String userName) {
        double amount = fineBalance.getOutstandingBalance(userID);

        if (fineBalance.payFine(userID)) {
            receipt.printReceipt(userID, userName, amount, fineBalance);
        } else {
            System.out.println("  No outstanding fines to pay.");
        }
    }
}