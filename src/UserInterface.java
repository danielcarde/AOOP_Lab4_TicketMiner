/**
 * UserInterface class - Taken from teamgd (Daniel and Gabriel)
 * Provides UI helper methods for displaying menus and clearing screens.
 */
public class UserInterface {

    /**
     * Prints the login menu.
     * Taken from teamgd.
     */
    public static void printLoginMenu(){
        System.out.println("---------------Ticket Miner---------------");
        System.out.println("               1) Customer");
        System.out.println("               2) Administrator");
        System.out.println("                   exit");
        System.out.println("------------------------------------------");
    }

    /**
     * Prints the administrator menu.
     * Taken from teamgd.
     */
    public static void printAdministratorMenu(){
        System.out.println("---------------Administrator---------------");
        System.out.println("               1) Inquiry Event");
        System.out.println("               2) Create Events");
        System.out.println("               3) Reciept/Invoices");
        System.out.println("                   exit");
        System.out.println("-------------------------------------------");
    }

    /**
     * Prints the inquiry events menu.
     * Taken from teamgd.
     */
    public static void printInquiryEvents(){
        System.out.println("-------------------------------------------");
        System.out.println("               1) Inquiry Event by ID");
        System.out.println("               2) Inquiry Event by Name");
        System.out.println("                   exit");
        System.out.println("-------------------------------------------");
    }

    /**
     * Prints the main menu.
     * Taken from teamgd.
     */
    public static void printMainMenu(){
        System.out.println("---------------Ticket Miner---------------");
        System.out.println("               1) Sports");
        System.out.println("               2) Concert");
        System.out.println("               3) Festival");
        System.out.println("                   exit");
        System.out.println("------------------------------------------");
    }

    /**
     * Clears the screen for better readability.
     * Taken from teamgd.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints the correct ID menu.
     * Taken from teamgd.
     */
    public static void correctIDMenu() {
        System.out.println("--------------Chose an Option--------------");
        System.out.println("          1) ID");
        System.out.println("          2) Name");
        System.out.println("          3) Date");
        System.out.println("          4) Time");
        System.out.println("          5) VIP Price");
        System.out.println("          6) Gold Price");
        System.out.println("          7) Silver Price");
        System.out.println("          8) Bronze Price");
        System.out.println("          9) General Admission Price");
        System.out.println("          10) View All Information");
        System.out.println("                      exit");
        System.out.println("-------------------------------------------");
    }
}

