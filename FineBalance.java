import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class FineBalance {

    private static final double FINE_RATE_PER_DAY = 0.50;
    private static final double LO`ST_ITEM_PENALTY = 50.00;
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

        // Constructor
        public FineRecord(String userID, String itemTitle, String fineType, double overdueAmount, double lostPenalty) {
            this.userID = userID;
            this.itemTitle = itemTitle;
            this.fineType = fineType;
            this.overdueAmount = overdueAmount;
            this.lostPenalty = lostPenalty;
            this.totalAmount = overdueAmount + lostPenalty;
            this.paid = false;
        }

        // Getters
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

        // Setter
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

    public void loadFines(){
        File file = new File(FINES_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] data = line.split(","); 
            if (data.length >= 6) {
                    FineRecord record = new FineRecord(data[0].trim(), data[1].trim(), data[2].trim(),
                            Double.parseDouble(data[3].trim()), Double.parseDouble(data[4].trim()));
                    record.setPaid(Boolean.parseBoolean(data[5].trim()));
                    fineRecords[recordCount++] = record;
                }
            }
        } catch (Exception e) {
            System.out.println("[System] Error loading fines data: " + e.getMessage());  
        }
    
    }

    private void saveFines() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FINES_FILE, false))) {
            for (int i = 0; i < recordCount; i++) {
                FineRecord r = fineRecords[i];
                pw.println(r.getUserID() + "," + r.getItemTitle() + "," + r.getFineType() + ","
                        + r.getOverdueAmount() + "," + r.getLostPenalty() + "," + r.isPaid());
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

        if (daysLate >= LOST_THRESHOLD_DAYS) {
            double overdueAmount = LOST_THRESHOLD_DAYS * FINE_RATE_PER_DAY;
            double total = overdueAmount + LOST_ITEM_PENALTY;

            fineRecords[recordCount++] = new FineRecord(
                    userID, itemTitle, "LOST", overdueAmount, LOST_ITEM_PENALTY);

            System.out.println("  [!] Item auto-marked as LOST (" + daysLate + " days late)");
            System.out.printf("  Overdue Fine (capped at %d days) : RM %.2f%n", LOST_THRESHOLD_DAYS, overdueAmount);
            System.out.printf("  Lost Penalty                     : RM %.2f%n", LOST_ITEM_PENALTY);
            System.out.printf("  Total Charged                    : RM %.2f%n", total);
            return total;

        } else {
            double fine = daysLate * FINE_RATE_PER_DAY;

            fineRecords[recordCount++] = new FineRecord(userID, itemTitle, "OVERDUE", fine, 0);
            saveFines();

            System.out.printf("  [Overdue Fine] '%s' — %d day(s) late. Fine: RM %.2f%n",
                    itemTitle, daysLate, fine);
            return fine;
        }
    }

    public boolean payFine(String userID) {
        double outstanding = getOutstandingBalance(userID);
        if (outstanding == 0)
            return false;

        boolean changed = false;
        for (int i = 0; i < recordCount; i++) {
            if (fineRecords[i].getUserID().equals(userID) && !fineRecords[i].isPaid()) {
                fineRecords[i].markPaid();
                changed = true;
            }
        }
        if (changed) {
            saveFines();
        }
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