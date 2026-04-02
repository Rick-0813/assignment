public class FineReport {

    private FineBalance fineBalance;
    private LibraryManager manager;

    // Constructor
    public FineReport(FineBalance fineBalance, LibraryManager manager) {
        this.fineBalance = fineBalance;
        this.manager = manager;
    }

    public void displayAllFinesWithNames() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        ALL FINE RECORDS                         |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records found.                        |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        System.out.printf("%n  %-6s  %-10s  %-18s  %-18s  %-7s  %-10s  %-8s  %-8s%n",
            "ID", "User ID", "Name", "Item Title", "Type", "Date", "Total RM", "Status");
        System.out.println("  ───────────────────────────────────────────────────────────────────────────────────────────────");

        FineBalance.FineRecord[] records = fineBalance.getFineRecords();
        for (int i = 0; i < count; i++) {
            FineBalance.FineRecord r = records[i];
            AdminLibrary user = manager.getUserByID(r.getUserID());
            String name = (user != null) ? user.getAdminName() : "Unknown";
            System.out.printf("  %-6s  %-10s  %-18s  %-18s  %-7s  %-10s  %-8.2f  %-8s%n",
                r.getFineID(), r.getUserID(), name, r.getItemTitle(),
                r.getFineType(), r.getDate(), r.getTotal(),
                r.isPaid() ? "PAID" : "UNPAID");
        }
        System.out.println("  ───────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void displayPendingFinesSorted() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        PENDING FINES  (Highest First)           |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records found.                        |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        FineBalance.FineRecord[] pending = new FineBalance.FineRecord[count];
        int pendingCount = 0;
        FineBalance.FineRecord[] records = fineBalance.getFineRecords();
        for (int i = 0; i < count; i++)
            if (!records[i].isPaid()) pending[pendingCount++] = records[i];

        if (pendingCount == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No pending fines.                             |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        for (int i = 0; i < pendingCount - 1; i++)
            for (int j = i + 1; j < pendingCount; j++)
                if (pending[j].getTotal() > pending[i].getTotal()) {
                    FineBalance.FineRecord temp = pending[i];
                    pending[i] = pending[j];
                    pending[j] = temp;
                }

        System.out.printf("%n  %-6s  %-10s  %-18s  %-18s  %-7s  %-10s  %-8s%n",
            "ID", "User ID", "Name", "Item Title", "Type", "Date", "Total RM");
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────");

        for (int i = 0; i < pendingCount; i++) {
            FineBalance.FineRecord r = pending[i];
            AdminLibrary user = manager.getUserByID(r.getUserID());
            String name = (user != null) ? user.getAdminName() : "Unknown";
            System.out.printf("  %-6s  %-10s  %-18s  %-18s  %-7s  %-10s  %-8.2f%n",
                r.getFineID(), r.getUserID(), name, r.getItemTitle(),
                r.getFineType(), r.getDate(), r.getTotal());
        }
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("  Total Pending: %d fines%n", pendingCount);
    }

    public void generateSummaryReport() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        FINES SUMMARY                            |");
        System.out.println("        |        Library Admin Report                     |");
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

        int usersWithFines = 0;
        for (int i = 0; i < uniqueCount; i++)
            if (fineBalance.hasPendingFines(seenUsers[i])) usersWithFines++;

        double paidPct = paidCount * 100.0 / count;
        double unpaidPct = unpaidCount * 100.0 / count;
        double overduePct = overdueCount * 100.0 / count;
        double lostPct = lostCount * 100.0 / count;

        System.out.println("        |                                                 |");
        System.out.println("        |  -- Records ----------------------------------  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Total Records    : %-28d|%n", count);
        System.out.printf("        |   Overdue Cases    : %-4d  (%.1f%%)               |%n", overdueCount, overduePct);
        System.out.printf("        |   Lost Cases       : %-4d  (%.1f%%)               |%n", lostCount, lostPct);
        System.out.println("        |                                                 |");
        System.out.printf("        |   Paid             : %-4d  (%.1f%%)               |%n", paidCount, paidPct);
        System.out.printf("        |   Unpaid           : %-4d  (%.1f%%)               |%n", unpaidCount, unpaidPct);
        System.out.println("        |                                                 |");
        System.out.println("        |  -- Financials --------------------------------  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Total Collected  : RM %-24.2f|%n", totalCollected);
        System.out.printf("        |   Total Outstanding: RM %-24.2f|%n", totalOutstanding);
        System.out.println("        |                                                 |");
        System.out.println("        |  -- Users -------------------------------------  |");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Unique Users     : %-28d|%n", uniqueCount);
        System.out.printf("        |   Users With Fines : %-28d|%n", usersWithFines);
        System.out.println("        |                                                 |");
        System.out.println("        |_________________________________________________|");
    }
}