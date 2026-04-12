import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

public class FineBalance {

    private static final double LOST_ITEM_PENALTY = 50.00;
    public static final int LOST_THRESHOLD_DAYS = 60;
    private static final int MAX_RECORDS = 1000;
    private static final String FINES_FILE = "fines_data.txt";

    private int nextFineNumber = 1;

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
        private int daysLate;

        // Constructor
        public FineRecord(String fineID, String userID, String itemTitle, String fineType,
            double overdueAmount, double lostPenalty, String date, int daysLate) {
            this.fineID = fineID;
            this.userID = userID;
            this.itemTitle = itemTitle;
            this.fineType = fineType;
            this.overdueAmount = overdueAmount;
            this.lostPenalty = lostPenalty;
            this.totalAmount = overdueAmount + lostPenalty;
            this.paid = false;
            this.date = date;
            this.daysLate = daysLate;  
        }

        // Getters
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
        public int getDaysLate() {                                  
            return daysLate;
        }

        // Setters
        public void setPaid(boolean p) {
            this.paid = p;
        }
        public void markPaid() {
            this.paid = true;
        }
    }

    private FineRecord[] fineRecords = new FineRecord[MAX_RECORDS];
    private int recordCount = 0;

    // Constructor
    public FineBalance() {
        loadFines();
    }

    private String generateFineID() {
        return String.format("FN%03d", nextFineNumber++);
    }

    public void loadFines() {
        File file = new File(FINES_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 7) {
                    try {
                        String fineID = data[0].trim();
                        String date = (data.length >= 8) ? data[7].trim() : LocalDate.now().toString();
                        int savedDaysLate = (data.length >= 9) ? Integer.parseInt(data[8].trim()) : 0;
                        FineRecord record = new FineRecord(
                            fineID, data[1].trim(), data[2].trim(), data[3].trim(),
                            Double.parseDouble(data[4].trim()),
                            Double.parseDouble(data[5].trim()), date, savedDaysLate);
                        record.setPaid(Boolean.parseBoolean(data[6].trim()));
                        fineRecords[recordCount++] = record;
                        int num = Integer.parseInt(fineID.replace("FN", ""));
                        if (num >= nextFineNumber) nextFineNumber = num + 1;
                    } catch (NumberFormatException e) {
                        System.out.println("  !! Skipping bad line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  !! Could not load fines: " + e.getMessage());
        }
    }

    private void saveFines() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FINES_FILE, false))) {
            for (int i = 0; i < recordCount; i++) {
                FineRecord r = fineRecords[i];
                pw.println(r.getFineID() + "," + r.getUserID() + "," + r.getItemTitle() + "," +
                    r.getFineType() + "," + r.getOverdueAmount() + "," + r.getLostPenalty() + "," +
                    r.isPaid() + "," + r.getDate() + "," + r.getDaysLate());
            }
            pw.flush();
        } catch (IOException e) {
            System.out.println("  !! Could not save fines.");
        }
    }

    public double processFine(String userID, String itemTitle, int daysLate, String userType) {
    if (daysLate <= 0) {
        System.out.println("  No fine. Item returned on time.");
        return 0.0;
    }

    String today = LocalDate.now().toString();
    String fineID = generateFineID();
    double rate = FineRate.getRatePerDay(userType);  // ← this is where rate comes from

    if (daysLate >= LOST_THRESHOLD_DAYS) {
        double overdueAmount = LOST_THRESHOLD_DAYS * rate;
        double total = overdueAmount + LOST_ITEM_PENALTY;
        fineRecords[recordCount++] = new FineRecord(fineID, userID, itemTitle, "LOST",
            overdueAmount, LOST_ITEM_PENALTY, today, daysLate);
        saveFines();
        System.out.println("  Item marked as LOST (" + daysLate + " days late)  Fine ID: " + fineID);
        System.out.printf("  Overdue (capped %d days @ RM%.2f/day): RM %.2f  |  Lost Penalty: RM %.2f  |  Total: RM %.2f%n",
            LOST_THRESHOLD_DAYS, rate, overdueAmount, LOST_ITEM_PENALTY, total);
        return total;
    } else {
        double fine = daysLate * rate;
        fineRecords[recordCount++] = new FineRecord(fineID, userID, itemTitle, "OVERDUE", fine, 0, today, daysLate);
        saveFines();
        System.out.printf("  Fine ID: %s | '%s' %d day(s) late @ RM%.2f/day | Total: RM %.2f%n",
            fineID, itemTitle, daysLate, rate, fine);
        return fine;
        }
    }

    public boolean payFine(String userID) {
        if (getOutstandingBalance(userID) == 0) return false;
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

    public FineRecord getFineByID(String fineID) {
        for (int i = 0; i < recordCount; i++)
            if (fineRecords[i].getFineID().equalsIgnoreCase(fineID)) return fineRecords[i];
        return null;
    }

    // Getters
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