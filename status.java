import java.io.File;
import java.util.Scanner;

public class Status {

    private static final String LOG_FILE = "system_logs.txt";

    private void printTableHeader() {
        System.out.printf("%n  %-19s  %-10s  %-15s  %-55s%n",
            "Timestamp", "User", "Action", "Details");
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    private void printTableFooter() {
        System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    private void printTableRow(String line) {
        try {
            String timestamp = line.substring(line.indexOf('[') + 1, line.indexOf(']')).trim();
            String[] parts = line.substring(line.indexOf(']') + 1).trim().split(" \\| ", 3);
            String user = parts[0].replace("User: ", "").trim();
            String action = parts[1].replace("Action: ", "").trim();
            String details = parts[2].replace("Details: ", "").trim();
            if (details.length() > 55) details = details.substring(0, 52) + "...";
            System.out.printf("  %-19s  %-10s  %-15s  %-55s%n", timestamp, user, action, details);
        } catch (Exception e) {
            System.out.println("  " + line);
        }
    }

    public void displayFullAuditLog() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        Full Audit Log                           |");
        System.out.println("        |_________________________________________________|");
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            boolean headerPrinted = false, found = false;
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
            if (!found) System.out.println("\n  Audit log is empty.");
            else printTableFooter();
        } catch (Exception e) {
            System.out.println("  Error reading log.");
        }
    }

    public void searchAuditByUserID(String userID) {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Audit Log - User: %-29s|%n", userID);
        System.out.println("        |_________________________________________________|");
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            boolean headerPrinted = false, found = false;
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
            if (!found) System.out.println("\n  No entries found for: " + userID);
            else printTableFooter();
        } catch (Exception e) {
            System.out.println("  Error reading log.");
        }
    }

    public void searchAuditByAction(String action) {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Audit Log - Action: %-27s|%n", action.toUpperCase());
        System.out.println("        |_________________________________________________|");
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            boolean headerPrinted = false, found = false;
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
            if (!found) System.out.println("\n  No entries found for: " + action.toUpperCase());
            else printTableFooter();
        } catch (Exception e) {
            System.out.println("  Error reading log.");
        }
    }
}