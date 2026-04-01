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
            System.out.println("\n               .==================================================.");
            System.out.println("               |           M y   F i n e   &   B i l l s          |");
            System.out.println("               |==================================================|");
            System.out.println("               |   Your fine balance is clear.                    |");
            System.out.println("               |   No outstanding fines. Keep it up! :)           |");
            System.out.println("               .==================================================.");
            return;
        }

        while (true) {
            System.out.println("\n               .==================================================.");
            System.out.println("               |           M y   F i n e   &   B i l l s          |");
            System.out.println("               |==================================================|");
            System.out.printf("               |   User ID  : %-36s|%n", userID);
            System.out.printf("               |   Name     : %-36s|%n", userName);
            System.out.printf("               |   Outstanding : RM %-30.2f|%n",
                fineBalance.getOutstandingBalance(userID));
            System.out.println("               |==================================================|");
            System.out.println("               |    [ 1 ] View Fine Details                       |");
            System.out.println("               |    [ 2 ] Pay All Fines                           |");
            System.out.println("               |    [ 0 ] Back                                    |");
            System.out.println("               .==================================================.");
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
                showFineDetails(userID, userName);
            } else if (choice == 2) {
                processFinePayment(userID, userName);
            } else {
                System.out.println("  Invalid choice.");
            }
        }
    }


    public void showFineDetails(String userID, String userName) {
        System.out.println("\n               .==================================================.");
        System.out.println("               |             F i n e   D e t a i l s              |");
        System.out.println("               |==================================================|");
        System.out.printf("               |   User ID : %-37s|%n", userID);
        System.out.printf("               |   Name    : %-37s|%n", userName);
        System.out.println("               |==================================================|");

        boolean found = false;
        int count = 1;

        for (int i = 0; i < fineBalance.getRecordCount(); i++) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                System.out.printf("               |   #%-2d  Item   : %-32s|%n", count, r.getItemTitle());
                System.out.printf("               |        Date   : %-32s|%n", r.getDate());
                System.out.printf("               |        Type   : %-32s|%n", r.getFineType());
                if (r.getFineType().equals("LOST")) {
                    System.out.printf("               |        Overdue: RM %-30.2f|%n", r.getOverdueAmount());
                    System.out.printf("               |        Penalty: RM %-30.2f|%n", r.getLostPenalty());
                }
                System.out.printf("               |        Total  : RM %-30.2f|%n", r.getTotal());
                System.out.printf("               |        Status : %-32s|%n", r.isPaid() ? "PAID" : "UNPAID");
                System.out.println("               |--------------------------------------------------|");
                found = true;
                count++;
            }
        }

        if (!found) {
            System.out.println("               |   No fine records found for this user.           |");
            System.out.println("               |--------------------------------------------------|");
        }
        System.out.printf("               |   Outstanding Balance : RM %-22.2f|%n",
            fineBalance.getOutstandingBalance(userID));
        System.out.println("               .==================================================.");
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
        System.out.println("\n               .==================================================.");
        System.out.println("               |         D e l e t e   F i n e   E n t r y        |");
        System.out.println("               |==================================================|");
        System.out.printf("               |   User ID : %-37s|%n", userID);
        System.out.printf("               |   Name    : %-37s|%n", userName);
        System.out.println("               |==================================================|");
        System.out.println("               |   (Newest entry shown first)                     |");
        System.out.println("               |--------------------------------------------------|");

        int[] indexMap = new int[1000];
        int displayCount = 0;


        for (int i = fineBalance.getRecordCount() - 1; i >= 0; i--) {
            FineBalance.FineRecord r = fineBalance.getFineRecords()[i];
            if (r.getUserID().equals(userID)) {
                displayCount++;
                indexMap[displayCount] = i;
                System.out.printf("               |   [%2d] %-15s | %-7s | %-10s | RM%-6.2f | %-6s |%n",
                    displayCount,
                    r.getItemTitle(),
                    r.getFineType(),
                    r.getDate(),
                    r.getTotal(),
                    r.isPaid() ? "PAID" : "UNPAID");
            }
        }

        if (displayCount == 0) {
            System.out.println("               |   No fine records found for this user.           |");
            System.out.println("               .==================================================.");
            return;
        }

        System.out.println("               |--------------------------------------------------|");
        System.out.println("               |   [ 0 ] Cancel                                   |");
        System.out.println("               .==================================================.");
        System.out.print("  Enter number to delete: ");

        if (!input.hasNextInt()) {
            System.out.println("  [!] Invalid input.");
            input.nextLine();
            return;
        }
        int pick = input.nextInt();
        input.nextLine();

        if (pick == 0) {
            System.out.println("  Cancelled.");
            return;
        }
        if (pick < 1 || pick > displayCount) {
            System.out.println("  [!] Invalid selection.");
            return;
        }

        int realIndex = indexMap[pick];
        FineBalance.FineRecord toDelete = fineBalance.getFineRecords()[realIndex];

        System.out.printf("  [!] Confirm delete: '%s' — RM %.2f ? (y/n): ",
            toDelete.getItemTitle(), toDelete.getTotal());
        String confirm = input.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            fineBalance.deleteFine(realIndex);
            System.out.println("  [OK] Fine record deleted successfully.");
            System.out.println("  [Note] A CORRECTION entry will be added to the audit log.");
        } else {
            System.out.println("  Cancelled. No changes made.");
        }
    }
}