public class LibraryUI {
    public static void printWelcomeLogo() {
        System.out.println("\n\n           ██████╗ ██╗██████╗ ██╗    ██╗     ██╗██████╗ ██████╗  █████╗ ██████╗ ██╗   ██╗       \n"+
                           "           ██╔══██╗██║██╔══██╗██║    ██║     ██║██╔══██╗██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝       \n"+
                           "           ██████╔╝██║██████╔╝██║    ██║     ██║██████╔╝██████╔╝███████║██████╔╝ ╚████╔╝        \n"+
                           "           ██╔══██╗██║██╔══██╗██║    ██║     ██║██╔══██╗██╔══██╗██╔══██║██╔══██╗  ╚██╔╝         \n"+
                           "           ██████╔╝██║██████╔╝██║    ███████╗██║██████╔╝██║  ██║██║  ██║██║  ██║   ██║          \n"+
                           "           ╚═════╝ ╚═╝╚═════╝ ╚═╝    ╚══════╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ");
    }

    public static void printLoginMenu() {
        printWelcomeLogo();
        System.out.println("===================================================================================================");
        System.out.println("\n                           .==================================================.");
        System.out.println("                           |   W e l c o m e   t o   B i b i   L i b r a r y  |");
        System.out.println("                           |==================================================|");
        System.out.println("                           |                                                  |");
        System.out.println("                           |    [ 1 ] Student                                 |");
        System.out.println("                           |    [ 2 ] Faculty                                 |");
        System.out.println("                           |    [ 3 ] Public Member                           |");
        System.out.println("                           |    [ 4 ] Admin (Login Required)                  |");
        System.out.println("                           |                                                  |");
        System.out.println("                           |--------------------------------------------------|");
        System.out.println("                           |                                                  |");
        System.out.println("                           |    [ 0 ] Exit                                    |");
        System.out.println("                           |                                                  |");
        System.out.println("                           '=================================================='");
    }

    public static void printRoleMenu(String userType) {
        System.out.println("=================================================================================================\n");
        System.out.println("               .==================================================.");
        System.out.println("              /                                                  /|");
        System.out.printf ("             /            Current Role: %-24s/ |%n", userType);
        System.out.println("            /                                                  /  |");
        System.out.println("           .==================================================.   |");

        if (userType.trim().equalsIgnoreCase("Admin")) {
            System.out.println("           |                                                  |   |");
            System.out.println("           |    [ 1 ] User Management                         |   |\n           |    [ 2 ] Circulation Module                      |   |\n           |    [ 3 ] Cataloging Admin                        |   |\n           |    [ 4 ] Fees and Audits                         |   .");
        } else {
            System.out.println("           |                                                  |   |");
            System.out.println("           |    [ 1 ] Search Catalog                          |   |\n           |    [ 2 ] My Loans                                |   |\n           |    [ 3 ] My Bills                                |   .");
        }
        System.out.println("           |                                                  |  /");
        System.out.println("           |--------------------------------------------------| /");
        System.out.println("           |    [ 0 ] Logout Back to Main Menu                |/");
        System.out.println("           '=================================================='");
        System.out.print("  Choice: ");
    }

    public static void printGoodbye() {                      
        System.out.println("\n                      .==============================================================.");
        System.out.println("                     /                                                              /");
        System.out.println("                    / Thank you for visiting Bibi Library. Have a great day! |^o^| / ");
        System.out.println("                   /                                                              /  ");
        System.out.println("                  '=============================================================='\n\n");
        

    }
}