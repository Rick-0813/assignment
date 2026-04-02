import java.io.File;
import java.util.Scanner;

public class Status {

    private static final String LOG_FILE = "system_logs.txt";

    private void printHeader() {
        System.out.printf("  %-4s  %-19s  %-10s  %-15s  %-55s%n",
            "Row", "Timestamp", "User", "Action", "Details");
        System.out.println("  ──────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    private void printRow(int rowNum, String line) {
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
            System.out.printf("  %-4d  %-19s  %-10s  %-15s  %-55s%n",
                rowNum, timestamp, user, action, details);
        } catch (Exception e) {
            System.out.printf("  %-4d  %s%n", rowNum, line);
        }
    }

    private void printFooter() {
        System.out.println("  ──────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    private String extractAction(String line) {
        try {
            String rest = line.substring(line.indexOf(']') + 1).trim();
            String[] parts = rest.split(" \\| ", 3);
            return parts[1].replace("Action: ", "").trim();
        } catch (Exception e) {
            return null;
        }
    }

    public void displayFullAuditLog() {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        FULL AUDIT LOG                           |");
        System.out.println("        |_________________________________________________|");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false, headerPrinted = false;
            int row = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (!headerPrinted) {
                    System.out.println();
                    printHeader();
                    headerPrinted = true;
                }
                printRow(row++, line);
                found = true;
            }
            if (!found) System.out.println("\n  Audit log is empty.");
            else printFooter();
        } catch (Exception e) {
            System.out.println("  !! Error reading log: " + e.getMessage());
        }
    }

    public void searchAuditByUserID(String userID) {
        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Audit Log  User: %-30s|%n", userID);
        System.out.println("        |_________________________________________________|");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false, headerPrinted = false;
            int row = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (line.toLowerCase().contains(userID.toLowerCase())) {
                    if (!headerPrinted) {
                        System.out.println();
                        printHeader();
                        headerPrinted = true;
                    }
                    printRow(row, line);
                    found = true;
                }
                row++;
            }
            if (!found) System.out.println("\n  No entries for User ID: " + userID);
            else printFooter();
        } catch (Exception e) {
            System.out.println("  !! Error reading log: " + e.getMessage());
        }
    }

    public void searchAuditByAction(Scanner input) {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("\n  No audit log file found.");
            return;
        }

        String[] uniqueActions = new String[100];
        int actionCount = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String action = extractAction(line);
                if (action == null) continue;
                boolean seen = false;
                for (int i = 0; i < actionCount; i++)
                    if (uniqueActions[i].equals(action)) {
                        seen = true;
                        break;
                    }
                if (!seen) uniqueActions[actionCount++] = action;
            }
        } catch (Exception e) {
            System.out.println("  !! Error reading log: " + e.getMessage());
            return;
        }

        if (actionCount == 0) {
            System.out.println("\n  Audit log is empty.");
            return;
        }

        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.println("        |        SEARCH AUDIT BY ACTION                   |");
        System.out.println("        |_________________________________________________|");
        System.out.println("        |                                                 |");
        for (int i = 0; i < actionCount; i++)
            System.out.printf("        |    [ %d ]  %-39s|%n", (i + 1), uniqueActions[i]);
        System.out.println("        |                                                 |");
        System.out.println("        |- - - - - - - - - - - - - - - - - - - - - - - - |");
        System.out.println("        |    [ 0 ]  Cancel                                |");
        System.out.println("        |_________________________________________________|");
        System.out.print("\n  Select action: ");

        if (!input.hasNextInt()) {
            input.nextLine();
            System.out.println("  Enter a number.");
            return;
        }
        int pick = input.nextInt();
        input.nextLine();

        if (pick == 0) {
            System.out.println("  Cancelled.");
            return;
        }
        if (pick < 1 || pick > actionCount) {
            System.out.println("  !! Invalid selection.");
            return;
        }

        String chosen = uniqueActions[pick - 1];

        System.out.println("\n        _________________________________________________");
        System.out.println("        |                                                 |");
        System.out.printf("        |   Audit Log  Action: %-28s|%n", chosen);
        System.out.println("        |_________________________________________________|");

        try (Scanner scanner = new Scanner(file)) {
            boolean found = false, headerPrinted = false;
            int row = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (line.toUpperCase().contains(chosen.toUpperCase())) {
                    if (!headerPrinted) {
                        System.out.println();
                        printHeader();
                        headerPrinted = true;
                    }
                    printRow(row, line);
                    found = true;
                }
                row++;
            }
            if (!found) System.out.println("\n  No entries for action: " + chosen);
            else printFooter();
        } catch (Exception e) {
            System.out.println("  !! Error reading log: " + e.getMessage());
        }
    }
}