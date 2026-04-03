public class FineReport {

    private FineBalance    fineBalance;
    private LibraryManager manager;

    // Constructor
    public FineReport(FineBalance fineBalance, LibraryManager manager) {
        this.fineBalance = fineBalance;
        this.manager     = manager;
    }

    public void displayAllFinesWithNames() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |               ALL FINE RECORDS                  |");
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
        System.out.println("  ───────────────────────────────────────────────────────────────────────────────────────────────────");

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
        System.out.println("  ───────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void displayPendingUserFines() {
        System.out.println("\n         _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        PENDING USER FINES                       |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records found.                        |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        String[] seenUsers = new String[1000];
        int uniqueCount = 0;
        FineBalance.FineRecord[] records = fineBalance.getFineRecords();

        for (int i = 0; i < count; i++) {
            if (records[i].isPaid()) continue;
            boolean seen = false;
            for (int j = 0; j < uniqueCount; j++)
                if (seenUsers[j].equals(records[i].getUserID())) { seen = true; break; }
            if (!seen) seenUsers[uniqueCount++] = records[i].getUserID();
        }

        if (uniqueCount == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No pending fines.                             |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        String[] userIDs    = new String[uniqueCount];
        String[] userNames  = new String[uniqueCount];
        int[]    fineCounts = new int[uniqueCount];
        double[] totals     = new double[uniqueCount];

        for (int i = 0; i < uniqueCount; i++) {
            userIDs[i]   = seenUsers[i];
            AdminLibrary u = manager.getUserByID(seenUsers[i]);
            userNames[i] = (u != null) ? u.getAdminName() : "Unknown";
            for (int j = 0; j < count; j++) {
                if (records[j].getUserID().equals(seenUsers[i]) && !records[j].isPaid()) {
                    totals[i] += records[j].getTotal();
                    fineCounts[i]++;
                }
            }
        }

        for (int i = 0; i < uniqueCount - 1; i++)
            for (int j = i + 1; j < uniqueCount; j++)
                if (totals[j] > totals[i]) {
                    double td = totals[i];     totals[i]     = totals[j];     totals[j]     = td;
                    int    tc = fineCounts[i]; fineCounts[i] = fineCounts[j]; fineCounts[j] = tc;
                    String ts = userIDs[i];    userIDs[i]    = userIDs[j];    userIDs[j]    = ts;
                    String tn = userNames[i];  userNames[i]  = userNames[j];  userNames[j]  = tn;
                }

        System.out.printf("%n  %-10s  %-20s  %-12s  %-14s%n",
            "User ID", "Name", "Fine (Qty)", "Total (RM)");
        System.out.println("  " + "─".repeat(62));

        for (int i = 0; i < uniqueCount; i++)
            System.out.printf("  %-10s  %-20s  %-12d  %-14.2f%n",
                userIDs[i], userNames[i], fineCounts[i], totals[i]);

        System.out.println("  " + "─".repeat(62));
        System.out.printf("  Users with pending fines: %d%n", uniqueCount);
    }

    public void generateSummaryReport() {
        System.out.println("\n         _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |           FINES SUMMARY                         |");
        System.out.println("        |           Library Admin Report                  |");
        System.out.println("        |_________________________________________________|");

        int count = fineBalance.getRecordCount();
        if (count == 0) {
            System.out.println("        |                                                 |");
            System.out.println("        |   No fine records on file.                      |");
            System.out.println("        |_________________________________________________|");
            return;
        }

        double totalCollected   = 0;
        double totalOutstanding = 0;
        int    overdueCount     = 0;
        int    lostCount        = 0;
        int    paidCount        = 0;
        int    unpaidCount      = 0;
        String[] seenUsers   = new String[1000];
        int      uniqueCount = 0;

        FineBalance.FineRecord[] records = fineBalance.getFineRecords();
        for (int i = 0; i < count; i++) {
            FineBalance.FineRecord r = records[i];
            if (r.isPaid()) { totalCollected   += r.getTotal(); paidCount++;   }
            else            { totalOutstanding += r.getTotal(); unpaidCount++; }
            if (r.getFineType().equals("LOST")) lostCount++;
            else                                overdueCount++;
            boolean seen = false;
            for (int j = 0; j < uniqueCount; j++)
                if (seenUsers[j].equals(r.getUserID())) { seen = true; break; }
            if (!seen) seenUsers[uniqueCount++] = r.getUserID();
        }

        int usersWithFines = 0;
        for (int i = 0; i < uniqueCount; i++)
            if (fineBalance.hasPendingFines(seenUsers[i])) usersWithFines++;

        double paidPct    = paidCount    * 100.0 / count;
        double unpaidPct  = unpaidCount  * 100.0 / count;
        double overduePct = overdueCount * 100.0 / count;
        double lostPct    = lostCount    * 100.0 / count;

        System.out.println("        |                                                 |");
        System.out.println("        |  ── Records ──────────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf ("        |   Total Records    : %-27d|%n", count);
        System.out.printf ("        |   Overdue Cases    : %-4d  (%.1f%%)              |%n", overdueCount, overduePct);
        System.out.printf ("        |   Lost Cases       : %-4d  (%.1f%%)              |%n", lostCount, lostPct);
        System.out.println("        |                                                 |");
        System.out.printf ("        |   Paid             : %-4d  (%.1f%%)              |%n", paidCount, paidPct);
        System.out.printf ("        |   Unpaid           : %-4d  (%.1f%%)              |%n", unpaidCount, unpaidPct);
        System.out.println("        |                                                 |");
        System.out.println("        |  ── Financials ───────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf ("        |   Total Collected  : RM %-24.2f|%n", totalCollected);
        System.out.printf ("        |   Total Outstanding: RM %-24.2f|%n", totalOutstanding);
        System.out.println("        |                                                 |");
        System.out.println("        |  ── Users ────────────────────────────────────  |");
        System.out.println("        |                                                 |");
        System.out.printf ("        |   Unique Users     : %-27d|%n", uniqueCount);
        System.out.printf ("        |   Users With Fines : %-27d|%n", usersWithFines);
        System.out.println("        |                                                 |");
        System.out.println("        |_________________________________________________|");
    }
}