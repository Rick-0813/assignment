import java.util.Scanner;

public class BibiLibrary {

    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n======== Welcome to Bibi Library ======== ");
            System.out.println(" 1. Personal   ");
            System.out.println(" 2. Book Type  ");
            System.out.println(" 3. My Loans & Status  ");
            System.out.println(" 4. Payment & Reports  ");
            System.out.println(" 0. Exit ");
            System.out.print("Please select an option: ");

            int choice = input.nextInt();
            input.nextLine(); 

            switch (choice) {
                case 1:
                    Personal();
                    break;
                case 2:
                    BookType();
                    break;
                case 3:
                    LoansAndStatus();
                    break;
                case 4:
                    PaymentAndReports();
                    break;
                case 0:
                    System.out.println("Thank you for using Bibi Library. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } 
    } 


    private static void Personal() {
        System.out.println("\n--- User and Reader Management ---");
        System.out.println("1. Add User");
        System.out.println("2. Remove User");
        System.out.println("3. View Users");
        System.out.print("Please select an option: ");
        
        int userChoice = input.nextInt();
        input.nextLine();

        switch (userChoice) {
            case 1: 
                System.out.println("Adding a new user..."); 
                break;
            case 2: 
                System.out.println("Removing a user..."); 
                break;
            case 3: 
                System.out.println("Viewing all users..."); 
                break;
            default: 
                System.out.println("Invalid choice.");
        }
    }

    private static void BookType() {
        //Chong Liang code
        System.out.println("\n--- Book Type Management ---");
    }


    private static void LoansAndStatus() {
        //Dennis code
        System.out.println("\n--- Circulation Module ---");
    }

    private static void PaymentAndReports() {
        //Samual code
        System.out.println("\n--- Payment and Reports ---");
    }
} 