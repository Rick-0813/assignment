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

    private static int idCounter = 1;

    public static class FineRecord {
        private String fineID;
        private String userID;
        private String itemTitle;
        private String fineType;
        private double overdueAmount;
        private double lostPenalty;
        private double totalAmount;
        private boolean paid;
        private String date;

        public FineRecord(String fineID, String userID, String itemTitle, String fineType,
            double overdueAmount, double lostPenalty, String date) {
            this.fineID = fineID;
            this.userID = userID;
            this.itemTitle = itemTitle;
            this.fineType = fineType;
            this.overdueAmount = overdueAmount;
            this.lostPenalty = lostPenalty;
            this.totalAmount = overdueAmount + lostPenalty;
            this.paid = false;
            this.date = date;
        }

        public String getFineID() {
            return fineID;
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
        public void setPaid(boolean p) {
            this.paid = p;
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
                String[] d = line.split(",");
                if (d.length >= 8) {
                    FineRecord r = new FineRecord(
                        d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(),
                        Double.parseDouble(d[4].trim()),
                        Double.parseDouble(d[5].trim()),
                        d[7].trim());
                    r.setPaid(Boolean.parseBoolean(d[6].trim()));
                    fineRecords[recordCount++] = r;
                    int num = Integer.parseInt(d[0].trim().substring(1));
                    if (num >= idCounter) idCounter = num + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("  [System Error] Failed to load fines.");
        }
    }

    private void saveFines() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FINES_FILE, false))) {
            for (int i = 0; i < recordCount; i++) {
                FineRecord r = fineRecords[i];
                pw.println(r.getFineID() + "," + r.getUserID() + "," + r.getItemTitle() + "," +
                    r.getFineType() + "," + r.getOverdueAmount() + "," + r.getLostPenalty() + "," +
                    r.isPaid() + "," + r.getDate());
            }
            pw.flush();
        } catch (IOException e) {
            System.out.println("  [System] Error saving fines.");
        }
    }

    private String generateID() {
        return "F" + String.format("%03d", idCounter++);
    }

    public double processFine(String userID, String itemTitle, int daysLate) {
        if (daysLate <= 0) {
            System.out.println("  No fine. Item returned on time.");
            return 0.0;
        }
        String today = LocalDate.now().toString();
        String id = generateID();
        if (daysLate >= LOST_THRESHOLD_DAYS) {
            double overdueAmount = LOST_THRESHOLD_DAYS * FINE_RATE_PER_DAY;
            double total = overdueAmount + LOST_ITEM_PENALTY;
            fineRecords[recordCount++] = new FineRecord(id, userID, itemTitle, "LOST", overdueAmount, LOST_ITEM_PENALTY, today);
            saveFines();
            System.out.println("  [!] Item marked as LOST (" + daysLate + " days late)");
            System.out.printf("  Overdue (capped %d days) : RM %.2f%n", LOST_THRESHOLD_DAYS, overdueAmount);
            System.out.printf("  Lost Penalty             : RM %.2f%n", LOST_ITEM_PENALTY);
            System.out.printf("  Total Charged            : RM %.2f%n", total);
            return total;
        } else {
            double fine = daysLate * FINE_RATE_PER_DAY;
            fineRecords[recordCount++] = new FineRecord(id, userID, itemTitle, "OVERDUE", fine, 0, today);
            saveFines();
            System.out.printf("  Fine for '%s': %d day(s) late = RM %.2f%n", itemTitle, daysLate, fine);
            return fine;
        }
    }

    public boolean payFine(String userID) {
        if (getOutstandingBalance(userID) == 0) return false;
        for (int i = 0; i < recordCount; i++)
            if (fineRecords[i].getUserID().equals(userID) && !fineRecords[i].isPaid())
                fineRecords[i].markPaid();
        saveFines();
        return true;
    }

    public boolean deleteFineByID(String fineID) {
        for (int i = 0; i < recordCount; i++) {
            if (fineRecords[i].getFineID().equalsIgnoreCase(fineID)) {
                for (int j = i; j < recordCount - 1; j++) fineRecords[j] = fineRecords[j + 1];
                fineRecords[recordCount - 1] = null;
                recordCount--;
                saveFines();
                return true;
            }
        }
        return false;
    }

    public double getOutstandingBalance(String userID) {
        double total = 0;
        for (int i = 0; i < recordCount; i++)
            if (fineRecords[i].getUserID().equals(userID) && !fineRecords[i].isPaid())
                total += fineRecords[i].getTotal();
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