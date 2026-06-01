/**
 * TicketMinerAdministrator class - Taken from teamgd (Daniel and Gabriel)
 * This provides an alternative administrator interface.
 * Note: This class depends on EventList and CustomerList from teamgd,
 * and expects Event classes to have methods like inquireEventDetails().
 * The base code uses RunTicket.java for admin functionality, but this class
 * is included as an alternative from teamgd.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TicketMinerAdministrator {
    /**
     * Administrator login method - Taken from teamgd
     */
    public static void administratorLogin() {
        Logger.getInstance().logAction("Administrator Logged In.");

        Scanner input = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            UserInterface.printAdministratorMenu();
            String adminMenuChoice = input.nextLine();
            switch (adminMenuChoice) {
                case "1":
                    mainMenuAdmin();
                    break;
                case "2":
                    eventManager();
                    break;
                case "3":
                    customerReceiptSummary();
                    break;
                case "exit":
                    UserInterface.clearScreen();
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                default:
                    UserInterface.clearScreen();
                    System.out.println("Error: Invalid Input");
                    System.out.println();
            }
        }
        Logger.getInstance().logAction("Administrator Logged Out.");
    }

    /**
     * Main admin menu - Taken from teamgd
     */
    public static void mainMenuAdmin() {
        EventList eventList = EventList.getInstance();

        Scanner input = new Scanner(System.in);

        boolean exit = false;

        while (!exit) {
            UserInterface.printMainMenu();
            String choice = input.nextLine();
            String eventChosen = "";
            switch (choice) {
                case "1":
                    UserInterface.clearScreen();
                    Logger.getInstance().logAction("Administrator Inquiring Sport Events.");
                    eventChosen = "Sport";
                    eventChosenAdmin(eventChosen);
                    break;
                case "2":
                    UserInterface.clearScreen();
                    Logger.getInstance().logAction("Administrator Inquiring Concert Events.");
                    eventChosen = "Concert";
                    eventChosenAdmin(eventChosen);
                    break;
                case "3":
                    UserInterface.clearScreen();
                    Logger.getInstance().logAction("Administrator Inquiring Festival Events.");
                    eventChosen = "Festival";
                    eventChosenAdmin(eventChosen);
                    break;
                case "exit":
                    UserInterface.clearScreen();
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                default:
                    UserInterface.clearScreen();
                    System.out.println("Error: Invalid Input");
                    System.out.println();
            }
        }
    }

    /**
     * Event chosen admin handler - Taken from teamgd
     */
    public static void eventChosenAdmin(String eventType) {
        EventList eventList = EventList.getInstance();
        Scanner input = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            UserInterface.printInquiryEvents();
            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "1":
                    inquireEvent(eventType);
                    Logger.getInstance().logAction("Administrator Inquiring Events by ID");
                    searchByID(eventType);
                    break;
                case "2":
                    inquireEvent(eventType);
                    Logger.getInstance().logAction("Administrator Inquiring Events by NAME");
                    searchByName(eventType);
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    UserInterface.clearScreen();
                    System.out.println("Invalid Input. Please Try Again.");
                    break;
            }
        }
    }

    /**
     * Inquire event method - Taken from teamgd
     */
    public static void inquireEvent(String eventType) {
        EventList eventList = EventList.getInstance();
        Scanner input = new Scanner(System.in);
        boolean correctInput = false;

        while (!correctInput) {
            System.out.println("Would You like to view " + eventType + " ID's and Names? (y or n)");
            String viewIds = input.nextLine();
            System.out.println("Type \"exit\" to exit otherwise");
            switch (viewIds) {
                case "y":
                    if (eventType.equals("Sport")) {
                        eventList.printSportEvents();
                    } else if (eventType.equals("Concert")) {
                        eventList.printConcertEvents();
                    } else if (eventType.equals("Festival")) {
                        eventList.printFestivalEvents();
                    } else {
                        System.out.println("Something Broke.");
                        return;
                    }
                    Logger.getInstance().logAction("Administrator Viewing ID's and Names");
                    correctInput = true;
                    break;
                case "n":
                case "exit":
                    correctInput = true;
                    break;
                default:
                    UserInterface.clearScreen();
                    System.out.println("Error: Invalid Input");
                    System.out.println();
                    break;
            }
        }
    }

    /**
     * Search by ID method - Taken from teamgd
     * Note: This expects Event.inquireEventDetails() method which may not exist in teammb Event class
     */
    public static void searchByID(String eventType) {
        EventList eventList = EventList.getInstance();
        Scanner input = new Scanner(System.in);
        Event chosenEvent = null;

        while (true) {
            System.out.print("Enter " + eventType + " Event ID: ");
            String idInput = input.nextLine();

            if (idInput.equalsIgnoreCase("exit")) {
                UserInterface.clearScreen();
                System.out.println("Returning to Main Menu...");
                return;
            }

            int idInputInt;
            try {
                idInputInt = Integer.parseInt(idInput);
            } catch (NumberFormatException e) {
                UserInterface.clearScreen();
                System.out.println("Error: Not a Valid ID!");
                continue;
            }

            if (eventType.equalsIgnoreCase("Sport")) {
                chosenEvent = (Event) eventList.searchSportByID(idInputInt);
            } else if (eventType.equalsIgnoreCase("Concert")) {
                chosenEvent = (Event) eventList.searchConcertByID(idInputInt);
            } else if (eventType.equalsIgnoreCase("Festival")) {
                chosenEvent = (Event) eventList.searchFestivalByID(idInputInt);
            } else {
                UserInterface.clearScreen();
                System.out.println("Something Broke.");
                return;
            }

            if (chosenEvent == null) {
                UserInterface.clearScreen();
                System.out.println("Event ID " + idInput + " not found.");
                continue;
            }

            Logger.getInstance().logAction("Administrator Inquiring " + eventType + " Event " + idInputInt);
            // Note: This expects Event.inquireEventDetails() method which may not exist
            // chosenEvent.inquireEventDetails(eventType);
            System.out.println("Note: inquireEventDetails() may need to be implemented");

            inquireSeatsSold(chosenEvent);
            break;
        }
    }

    /**
     * Search by name method - Taken from teamgd
     * Note: This expects Event.inquireEventDetails() method which may not exist in teammb Event class
     */
    public static void searchByName(String eventType) {
        EventList eventList = EventList.getInstance();
        Scanner input = new Scanner(System.in);
        Event chosenEvent = null;

        while (true) {
            System.out.print("Enter " + eventType + " Event Name: ");
            String nameInput = input.nextLine();

            if (nameInput.equalsIgnoreCase("exit")) {
                UserInterface.clearScreen();
                System.out.println("Returning to Main Menu...");
                return;
            }

            if (eventType.equalsIgnoreCase("Sport")) {
                chosenEvent = (Event) eventList.searchSportByName(nameInput);
            } else if (eventType.equalsIgnoreCase("Concert")) {
                chosenEvent = (Event) eventList.searchConcertByName(nameInput);
            } else if (eventType.equalsIgnoreCase("Festival")) {
                chosenEvent = (Event) eventList.searchFestivalByName(nameInput);
            } else {
                UserInterface.clearScreen();
                System.out.println("Something Broke.");
                return;
            }

            if (chosenEvent == null) {
                UserInterface.clearScreen();
                System.out.println("Event Name " + nameInput + " not found.");
                continue;
            }

            Logger.getInstance().logAction("Administrator Inquiring " + eventType + " Event " + nameInput);
            // Note: This expects Event.inquireEventDetails() method which may not exist
            // chosenEvent.inquireEventDetails(eventType);
            System.out.println("Note: inquireEventDetails() may need to be implemented");

            inquireSeatsSold(chosenEvent);
            break;
        }
    }

    /**
     * Inquire seats sold method - Taken from Gabriel Tovar (teamgd)
     * Note: This expects Venue methods like getVipSoldSeats() which may not exist in teammb Venue class
     */
    public static void inquireSeatsSold(Event event) {
        Scanner scan = new Scanner(System.in);
        boolean runSubMenu = true;
        String inquireInput;

        while (runSubMenu) {
            System.out.println("\nC. View Total Seats Sold"
                    + "\nD. View VIP Seats Sold"
                    + "\nE. View Gold Seats Sold"
                    + "\nF. View Silver Seats Sold"
                    + "\nG. View Bronze Seats Sold"
                    + "\nH. View General Admission Seats Sold"
                    + "\nType EXIT to return to previous menu.");

            inquireInput = scan.nextLine().toUpperCase();

            if (inquireInput.equalsIgnoreCase("EXIT")) {
                runSubMenu = false;
                break;
            }

            switch (inquireInput) {
                case "C":
                    // Note: This expects Venue methods that may not exist in teammb Venue class
                    // int totalSold = event.getVenue().getVipSoldSeats() + event.getVenue().getGoldSoldSeats()
                    //         + event.getVenue().getSilverSoldSeats() + event.getVenue().getBronzeSoldSeats()
                    //         + event.getVenue().getGeneralAdSoldSeats();
                    // System.out.println("Total Seats Sold: " + totalSold);
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                case "D":
                    // System.out.println("VIP Seats Sold: " + event.getVenue().getVipSoldSeats());
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                case "E":
                    // System.out.println("Gold Seats Sold: " + event.getVenue().getGoldSoldSeats());
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                case "F":
                    // System.out.println("Silver Seats Sold: " + event.getVenue().getSilverSoldSeats());
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                case "G":
                    // System.out.println("Bronze Seats Sold: " + event.getVenue().getBronzeSoldSeats());
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                case "H":
                    // System.out.println("General Admission Seats Sold: " + event.getVenue().getGeneralAdSoldSeats());
                    System.out.println("Note: Venue seat tracking methods may need to be implemented");
                    break;
                default:
                    System.out.println("Invalid selection. Choose C-H or type EXIT.");
                    break;
            }
        }
    }

    /**
     * Event manager method - Taken from teamgd
     * Note: This depends on EventList.createEvent() which may need adaptation
     */
    public static void eventManager() {
        EventList eventList = EventList.getInstance();
        Scanner input = new Scanner(System.in);

        System.out.println("-------Creating New Event-------");
        int newID = eventList.getNextEventID();
        System.out.println("Assigned Event ID: " + newID);

        System.out.print("Enter Event Name: ");
        String name = input.nextLine();

        System.out.print("Enter Event Date (MM/DD/YYYY): ");
        String date = input.nextLine();

        System.out.print("Enter Event Time (HH:MM AM/PM): ");
        String time = input.nextLine();

        System.out.println("Select Venue:");
        List<String> venueNames = new ArrayList<>(eventList.getVenueList().keySet());
        Collections.sort(venueNames);
        for (int i = 0; i < venueNames.size(); i++) {
            System.out.println((i + 1) + ". " + venueNames.get(i));
        }

        int venueChoice = -1;
        while (venueChoice < 1 || venueChoice > venueNames.size()) {
            System.out.print("Enter choice number: ");
            if (input.hasNextInt()) {
                venueChoice = input.nextInt();
                input.nextLine();
            } else {
                input.nextLine();
            }
        }
        String selectedVenueName = venueNames.get(venueChoice - 1);
        Venue venue = eventList.getVenueByName(selectedVenueName);

        double generalAdPrice = -1;
        while (generalAdPrice < 0 || generalAdPrice > 4000) {
            System.out.print("Enter General Admission Price (max of $4000): ");
            if (input.hasNextDouble()) {
                generalAdPrice = input.nextDouble();
                input.nextLine();
            } else {
                input.nextLine();
            }
        }

        double VIPPrice = generalAdPrice * 5;
        double goldPrice = generalAdPrice * 3;
        double silverPrice = generalAdPrice * 2.5;
        double bronzePrice = generalAdPrice * 1.5;

        boolean fireworksPlanned = false;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Will there be fireworks? (true/false): ");
            if (input.hasNextBoolean()) {
                fireworksPlanned = input.nextBoolean();
                input.nextLine();
                validInput = true;
            } else {
                System.out.println("Invalid input. Please enter true or false.");
                input.nextLine();
            }
        }

        int vipSeats = (int) (venue.getCapacity() * 0.05);
        int goldSeats = (int) (venue.getCapacity() * 0.10);
        int silverSeats = (int) (venue.getCapacity() * 0.15);
        int bronzeSeats = (int) (venue.getCapacity() * 0.20);
        int generalSeats = (int) (venue.getCapacity() * 0.45);
        int reservedExtraSeats = (int) (venue.getCapacity() * 0.05);

        System.out.println("Select Event Type: 1. Sport  2. Concert  3. Festival");
        int typeChoice = 0;
        String eventType = "";
        while (typeChoice < 1 || typeChoice > 3) {
            System.out.print("Enter choice number: ");
            if (input.hasNextInt()) {
                typeChoice = input.nextInt();
                input.nextLine();
                switch (typeChoice) {
                    case 1:
                        eventType = "Sport";
                        break;
                    case 2:
                        eventType = "Concert";
                        break;
                    case 3:
                        eventType = "Festival";
                        break;
                }
            } else {
                input.nextLine();
            }
        }

        eventList.createEvent(newID, eventType, name, date, time, VIPPrice, goldPrice, silverPrice, bronzePrice,
                generalAdPrice, selectedVenueName);

        System.out.println("\n---- Event Created ----");
        System.out.println("ID: " + newID);
        System.out.println("Name: " + name);
        System.out.println("Date: " + date + " Time: " + time);
        System.out.println("Venue: " + selectedVenueName + " (Capacity: " + venue.getCapacity() + ")");
        System.out.println("Ticket Prices: VIP $" + VIPPrice + ", Gold $" + goldPrice + ", Silver $" + silverPrice
                + ", Bronze $" + bronzePrice + ", GA $" + generalAdPrice);
        System.out.println("Seat Distribution: VIP " + vipSeats + ", Gold " + goldSeats + ", Silver " + silverSeats
                + ", Bronze " + bronzeSeats + ", GA " + generalSeats + ", Reserved Extra " + reservedExtraSeats);
        System.out.println("Fireworks Planned: " + fireworksPlanned);
        System.out.println("Total Event Cost: $" + (venue.getCost() + (fireworksPlanned ? 0 : 0))); // Note: fireworksCost may need to be accessed differently
    }

    /**
     * Customer receipt summary method - Taken from teamgd
     */
    public static void customerReceiptSummary() {
        Scanner input = new Scanner(System.in);
        CustomerList customerList = CustomerList.getInstance();

        System.out.println("Enter the username of the customer to generate receipt summary (or type EXIT to cancel):");
        String username = input.nextLine();

        if (username.equalsIgnoreCase("EXIT")) {
            UserInterface.clearScreen();
            return;
        }

        Customer customer = customerList.searchCustomerByUsername(username);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        List<Invoice> invoices = customer.getInvoices();
        if (invoices == null || invoices.isEmpty()) {
            System.out.println("No purchases found for this customer.");
            return;
        }

        invoices.sort((i1, i2) -> i1.getPurchaseDate().compareTo(i2.getPurchaseDate()));

        System.out.println("Choose an option:");
        System.out.println("1. Latest Purchase Receipt");
        System.out.println("2. Full Invoice Summary");
        String choice = input.nextLine();

        switch (choice) {
            case "1":
                customerList.writeCustomerLatestReceipt(username);
                System.out.println("Latest receipt saved as " + username + "_Receipt.txt");
                Logger.getInstance().logAction("Admin generated latest receipt for " + username);
                break;
            case "2":
                customerList.writeCustomerInvoiceSummary(username);
                System.out.println("Invoice summary saved as " + username + "_InvoiceSummary.txt");
                Logger.getInstance().logAction("Admin generated invoice summary for " + username);
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }

        System.out.println("Press Enter to continue...");
        input.nextLine();
    }
}

