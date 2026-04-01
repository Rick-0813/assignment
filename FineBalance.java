import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

public class FineBalance {

    private static final double FINE_RATE_PER_DAY = 0.50;
    private static final double LOST_ITEM_PENALTY = 50.00;
    private static final int LOST_THRESHOLD_DAYS = 30;
    private static final int MAX_RECORDS = 1000;
    private static final String FINES_FILE = "fines_data.txt";


    public static class FineRecord {

        private String userID;
        private String itemTitle;
        private String fineType;
        private double overdueAmount;
        private double lostPenalty;
        private double totalAmount;
        private boolean paid;
        private String date;

        public FineRecord(String userID, String itemTitle, String fineType,
            double overdueAmount, double lostPenalty, String date) {
            this.userID = userID;
            this.itemTitle = itemTitle;
            this.fineType = fineType;
            this.overdueAmount = overdueAmount;
            this.lostPenalty = lostPenalty;
            this.totalAmount = overdueAmount + lostPenalty;
            this.paid = false;
            this.date = date;
        }

        public String getUserID() {
            return userID;
        }
        public String getItemTitle() {
            return itemTitle;
        }
        public String getFineType() {
            return fineType;
        }
        public double getOverdueAmount() {
            return overdueAmount;
        }
        public double getLostPenalty() {
            return lostPenalty;
        }
        public double getTotal() {
            return totalAmount;
        }
        public boolean isPaid() {
            return paid;
        }
        public String getDate() {
            return date;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }
        public void markPaid() {
            this.paid = true;
        }
    }

    private FineRecord[] fineRecords = new FineRecord[MAX_RECORDS];
    private int recordCount = 0;

    public FineBalance() {
        loadFines();
    }


    public void loadFines() {
        File file = new File(FINES_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        String date = (data.length >= 7) ? data[6].trim() : LocalDate.now().toString();

                        FineRecord record = new FineRecord(
                            data[0].trim(), data[1].trim(), data[2].trim(),
                            Double.parseDouble(data[3].trim()),
                            Double.parseDouble(data[4].trim()),
                            date);
                        record.setPaid(Boolean.parseBoolean(data[5].trim()));
                        fineRecords[recordCount++] = record;

                    } catch (NumberFormatException e) {
                        System.out.println("  [!!System Warning] Skipping bad line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  [System Error] Error loading fines: " + e.getMessage());
        }
    }


    private void saveFines() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FINES_FILE, false))) {
            for (int i = 0; i < recordCount; i++) {
                FineRecord r = fineRecords[i];
                pw.println(r.getUserID() + "," +
                    r.getItemTitle() + "," +
                    r.getFineType() + "," +
                    r.getOverdueAmount() + "," +
                    r.getLostPenalty() + "," +
                    r.isPaid() + "," +
                    r.getDate());
            }
            pw.flush();
        } catch (IOException e) {
            System.out.println("  [System] Error saving fines data.");
        }
    }

    public double processFine(String userID, String itemTitle, int daysLate) {
        if (daysLate <= 0) {
            System.out.println("  No fine. Item returned on time.");
            return 0.0;
        }

        String today = LocalDate.now().toString();

        if (daysLate >= LOST_THRESHOLD_DAYS) {
            double overdueAmount = LOST_THRESHOLD_DAYS * FINE_RATE_PER_DAY;
            double total = overdueAmount + LOST_ITEM_PENALTY;

            fineRecords[recordCount++] = new FineRecord(
                userID, itemTitle, "LOST", overdueAmount, LOST_ITEM_PENALTY, today);
            saveFines();

            System.out.println("  [!] Item auto-marked as LOST (" + daysLate + " days late)");
            System.out.printf("  Overdue Fine (capped at %d days) : RM %.2f%n", LOST_THRESHOLD_DAYS, overdueAmount);
            System.out.printf("  Lost Penalty                     : RM %.2f%n", LOST_ITEM_PENALTY);
            System.out.printf("  Total Charged                    : RM %.2f%n", total);
            return total;

        } else {
            double fine = daysLate * FINE_RATE_PER_DAY;

            fineRecords[recordCount++] = new FineRecord(
                userID, itemTitle, "OVERDUE", fine, 0, today);
            saveFines();

            System.out.printf("  [Overdue Fine] '%s' — %d day(s) late. Fine: RM %.2f%n",
                itemTitle, daysLate, fine);
            return fine;
        }
    }

    public void displayAllFines() {
        System.out.println("\n             .======================================================================.");
        System.out.println("             |                   A L L   F I N E   R E C O R D S                   |");
        System.out.println("             |======================================================================|");

        if (recordCount == 0) {
            System.out.println("             |   No fine records found.                                             |");
            System.out.println("             .======================================================================.");
            return;
        }

        System.out.printf("             | %-3s | %-10s | %-20s | %-7s | %-10s | %-8s | %-6s |%n",
            "No.", "User ID", "Item Title", "Type", "Date", "Total", "Status");
        System.out.println("             |======================================================================|");

        for (int i = 0; i < recordCount; i++) {
            FineRecord r = fineRecords[i];
            System.out.printf("             | %-3d | %-10s | %-20s | %-7s | %-10s | RM%-5.2f | %-6s |%n",
                (i + 1),
                r.getUserID(),
                r.getItemTitle(),
                r.getFineType(),
                r.getDate(),
                r.getTotal(),
                r.isPaid() ? "PAID" : "UNPAID");
        }
        System.out.println("             .======================================================================.");
    }

    public void generateSummaryReport() {
        System.out.println("\n             .==================================================.");
        System.out.println("             |      F I N E S   S U M M A R Y   R E P O R T     |");
        System.out.println("             |==================================================|");

        if (recordCount == 0) {
            System.out.println("             |   No fine records on file.                       |");
            System.out.println("             .==================================================.");
            return;
        }

        double totalCollected = 0;
        double totalOutstanding = 0;
        int overdueCount = 0;
        int lostCount = 0;
        int paidCount = 0;
        int unpaidCount = 0;

        String[] seenUsers = new String[MAX_RECORDS];
        int uniqueCount = 0;

        for (int i = 0; i < recordCount; i++) {
            FineRecord r = fineRecords[i];

            if (r.isPaid()) {
                totalCollected += r.getTotal();
                paidCount++;
            } else {
                totalOutstanding += r.getTotal();
                unpaidCount++;
            }

            if (r.getFineType().equals("LOST")) lostCount++;
            else overdueCount++;

            boolean alreadySeen = false;
            for (int j = 0; j < uniqueCount; j++) {
                if (seenUsers[j].equals(r.getUserID())) {
                    alreadySeen = true;
                    break;
                }
            }
            if (!alreadySeen) seenUsers[uniqueCount++] = r.getUserID();
        }

        String highestUser = "None";
        double highestBalance = 0;
        for (int i = 0; i < uniqueCount; i++) {
            double bal = getOutstandingBalance(seenUsers[i]);
            if (bal > highestBalance) {
                highestBalance = bal;
                highestUser = seenUsers[i];
            }
        }

        int usersWithFines = 0;
        for (int i = 0; i < uniqueCount; i++) {
            if (hasPendingFines(seenUsers[i])) usersWithFines++;
        }

        System.out.println("             |                                                  |");
        System.out.printf("             |   Total Fine Records     : %-22d|%n", recordCount);
        System.out.printf("             |   Overdue Cases          : %-22d|%n", overdueCount);
        System.out.printf("             |   Lost Item Cases        : %-22d|%n", lostCount);
        System.out.println("             |--------------------------------------------------|");
        System.out.printf("             |   Paid Records           : %-22d|%n", paidCount);
        System.out.printf("             |   Unpaid Records         : %-22d|%n", unpaidCount);
        System.out.println("             |--------------------------------------------------|");
        System.out.printf("             |   Total Collected (Paid) : RM %-19.2f|%n", totalCollected);
        System.out.printf("             |   Total Outstanding      : RM %-19.2f|%n", totalOutstanding);
        System.out.println("             |--------------------------------------------------|");
        System.out.printf("             |   Users With Fines       : %-22d|%n", usersWithFines);

        if (highestBalance > 0) {
            System.out.printf("             |   Highest Owing User     : %-22s|%n", highestUser);
            System.out.printf("             |   Their Balance          : RM %-19.2f|%n", highestBalance);
        } else {
            System.out.println("             |   Highest Owing User     : All clear!               |");
        }

        System.out.println("             |                                                  |");
        System.out.println("             .==================================================.");
    }


    public boolean payFine(String userID) {
        double outstanding = getOutstandingBalance(userID);
        if (outstanding == 0) return false;

        boolean changed = false;
        for (int i = 0; i < recordCount; i++) {
            if (fineRecords[i].getUserID().equals(userID) && !fineRecords[i].isPaid()) {
                fineRecords[i].markPaid();
                changed = true;
            }
        }
        if (changed) saveFines();
        return true;
    }


    public boolean deleteFine(int index) {
        if (index < 0 || index >= recordCount) return false;

        for (int i = index; i < recordCount - 1; i++) {
            fineRecords[i] = fineRecords[i + 1];
        }
        fineRecords[recordCount - 1] = null;
        recordCount--;
        saveFines();
        return true;
    }

    public double getOutstandingBalance(String userID) {
        double total = 0;
        for (int i = 0; i < recordCount; i++) {
            if (fineRecords[i].getUserID().equals(userID) && !fineRecords[i].isPaid()) {
                total += fineRecords[i].getTotal();
            }
        }
        return total;
    }

    public boolean hasPendingFines(String userID) {
        return getOutstandingBalance(userID) > 0;
    }

    public FineRecord[] getFineRecords() {
        return fineRecords;
    }
    public int getRecordCount() {
        return recordCount;
    }
}