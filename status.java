import java.io.File;
import java.util.Scanner;

public class Status {

    private static final String LOG_FILE = "system_logs.txt";

    private void printTableHeader() {
        System.out.println("  .-----------------------------------------------------------------------------------------------------------------------------------.");
        System.out.printf("  | %-19s | %-10s | %-15s | %-55s |%n",
            "Timestamp", "User", "Action", "Details");
        System.out.println("  |-----------------------------------------------------------------------------------------------------------------------------------|");
    }

    private void printTableFooter() {
        System.out.println("  '-----------------------------------------------------------------------------------------------------------------------------------'");
    }


    private void printTableRow(String line) {
        try {

            int tsStart = line.indexOf('[') + 1;
            int tsEnd = line.indexOf(']');
            String timestamp = line.substring(tsStart, tsEnd).trim();


            String rest = line.substring(tsEnd + 1).trim();
            String[] parts = rest.split(" \\| ", 3);

            String user = parts[0].replace("User: ", "").trim();
            String action = parts[1].replace("Action: ", "").trim();
            String details = parts[2].replace("Details: ", "").trim();


            if (details.length() > 55) details = details.substring(0, 52) + "...";

            System.out.printf("  | %-19s | %-10s | %-15s | %-55s |%n",
                timestamp, user, action, details);

        } catch (Exception e) {

            System.out.println("  | (raw) " + line);
        }
    }

    public void displayFullAuditLog() {
        System.out.println("\n             .==================================================.");
        System.out.println("             |            F U L L   A U D I T   L O G           |");
        System.out.println("             .==================================================.\n");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("  No audit log file found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false;
            boolean headerPrinted = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                if (!headerPrinted) {
                    printTableHeader();
                    headerPrinted = true;
                }
                printTableRow(line);
                found = true;
            }

            if (!found) {
                System.out.println("  Audit log is empty.");
            } else {
                printTableFooter();
            }

        } catch (Exception e) {
            System.out.println("  Error reading log: " + e.getMessage());
        }
    }


    public void searchAuditByUserID(String userID) {
        System.out.println("\n             .==================================================.");
        System.out.printf("             |   Audit Log — User: %-30s|%n", userID);
        System.out.println("             .==================================================.\n");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("  No audit log file found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false;
            boolean headerPrinted = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.toLowerCase().contains(userID.toLowerCase())) {
                    if (!headerPrinted) {
                        printTableHeader();
                        headerPrinted = true;
                    }
                    printTableRow(line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("  No entries found for User ID: " + userID);
            } else {
                printTableFooter();
            }

        } catch (Exception e) {
            System.out.println("  Error reading log: " + e.getMessage());
        }
    }

    public void searchAuditByAction(String action) {
        System.out.println("\n             .==================================================.");
        System.out.printf("             |   Audit Log — Action: %-28s|%n", action.toUpperCase());
        System.out.println("             .==================================================.\n");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("  No audit log file found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false;
            boolean headerPrinted = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.toUpperCase().contains(action.toUpperCase())) {
                    if (!headerPrinted) {
                        printTableHeader();
                        headerPrinted = true;
                    }
                    printTableRow(line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("  No entries found for action: " + action.toUpperCase());
            } else {
                printTableFooter();
            }

        } catch (Exception e) {
            System.out.println("  Error reading log: " + e.getMessage());
        }
    }
}