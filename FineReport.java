public class FineReport {

    private FineBalance fineBalance;
    private LibraryManager manager;

    public FineReport(FineBalance fineBalance, LibraryManager manager) {
        this.fineBalance = fineBalance;
        this.manager = manager;
    }

    public void displayAllFinesWithNames() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        All Fine Records                         |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records found.                        |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        System.out.printf("%n  %-5s  %-10s  %-18s  %-18s  %-8s  %-10s  %-8s  %-8s%n",
            "ID", "User ID", "Name", "Item Title", "Type", "Date", "Total RM", "Status");
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────────────────");

        FineBalance.FineRecord[] records = fineBalance.getFineRecords();
        for (int i = 0; i < count; i++) {
            FineBalance.FineRecord r = records[i];
            AdminLibrary user = manager.getUserByID(r.getUserID());
            String name = (user != null) ? user.getAdminName() : "Unknown";
            System.out.printf("  %-5s  %-10s  %-18s  %-18s  %-8s  %-10s  %-8.2f  %-8s%n",
                r.getFineID(), r.getUserID(), name, r.getItemTitle(),
                r.getFineType(), r.getDate(), r.getTotal(),
                r.isPaid() ? "PAID" : "UNPAID");
        }
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void generateSummaryReport() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        Fines Summary Report                     |");
        System.out.println("        |        Library Admin                            |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records on file.                      |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        double totalCollected = 0;
        double totalOutstanding = 0;
        int overdueCount = 0;
        int lostCount = 0;
        int paidCount = 0;
        int unpaidCount = 0;
        String[] seenUsers = new String[1000];
        int uniqueCount = 0;

        FineBalance.FineRecord[] records = fineBalance.getFineRecords();
        for (int i = 0; i < count; i++) {
            FineBalance.FineRecord r = records[i];
            if (r.isPaid()) {
                totalCollected += r.getTotal();
                paidCount++;
            } else {
                totalOutstanding += r.getTotal();
                unpaidCount++;
            }
            if (r.getFineType().equals("LOST")) lostCount++;
            else overdueCount++;
            boolean seen = false;
            for (int j = 0; j < uniqueCount; j++)
                if (seenUsers[j].equals(r.getUserID())) {
                    seen = true;
                    break;
                }
            if (!seen) seenUsers[uniqueCount++] = r.getUserID();
        }

        String highestUser = "None";
        double highestBalance = 0;
        int usersWithFines = 0;
        for (int i = 0; i < uniqueCount; i++) {
            double bal = fineBalance.getOutstandingBalance(seenUsers[i]);
            if (bal > 0) usersWithFines++;
            if (bal > highestBalance) {
                highestBalance = bal;
                highestUser = seenUsers[i];
            }
        }
        AdminLibrary topUser = manager.getUserByID(highestUser);
        String topName = (topUser != null) ? topUser.getAdminName() : highestUser;

        System.out.println("        |                                                 |");
        System.out.println("        |  ── Records ──────────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Total Fine Records     : %-21d|%n", count);
        System.out.printf("        |   Overdue Cases          : %-21d|%n", overdueCount);
        System.out.printf("        |   Lost Item Cases        : %-21d|%n", lostCount);
        System.out.println("        |                                                 |");
        System.out.printf("        |   Paid Records           : %-21d|%n", paidCount);
        System.out.printf("        |   Unpaid Records         : %-21d|%n", unpaidCount);
        System.out.println("        |                                                 |");
        System.out.println("        |  ── Financials ────────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Total Collected (Paid) : RM %-18.2f|%n", totalCollected);
        System.out.printf("        |   Total Outstanding      : RM %-18.2f|%n", totalOutstanding);
        System.out.println("        |                                                 |");
        System.out.println("        |  ── Users ─────────────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Users With Fines       : %-21d|%n", usersWithFines);
        if (highestBalance > 0) {
            System.out.printf("        |   Highest Owing          : %-21s|%n", topName);
            System.out.printf("        |   Their Balance          : RM %-18.2f|%n", highestBalance);
        } else {
            System.out.println("        |   Highest Owing          : All clear!              |");
        }
        System.out.println("        |                                                 |");
        System.out.println("        |_________________________________________________|");
    }
}