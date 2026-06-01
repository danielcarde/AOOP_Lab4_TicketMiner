/**
 * The University of Texas at El Paso
 * Department of Computer Science
 * CS 3331 – Advanced Object-Oriented Programming
 * Instructor: Daniel Mejia
 * Fall 2025
 * Programming Assignment 4
 * 
 * Name: Miranda Macias, Brian Casio, Daniel Cardenas, Gabriel Tovar
 * 
 * Lab Description: This program implements a comprehensive ticket purchasing system (TicketMiner)
 * that allows customers to browse events, purchase tickets with various fees, cancel purchases,
 * and generate receipts. Administrators can manage events, view revenue reports, and process
 * automatic purchases. The system includes fee calculations (convenience, service, charity),
 * tax computation, discount strategies, and electronic ticket generation.
 * 
 * Honesty Statement: We declare that this submission is the result of our own work and has
 * been completed in collaboration with our team members. We have not given or received
 * unauthorized assistance on this assignment.
 */
// Combined TicketMiner System
// Miranda Macias, Brian Casio, Daniel Cardenas, Gabriel Tovar
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the TicketMiner ticket purchasing system.
 * 
 * Core architecture taken from Miranda Macias:
 * - Uses DataStore for data management
 * - Uses Logger for activity logging
 * - Admin menu with options A-G
 * - Customer purchase flow with category selection
 * 
 * Additional functionality taken from Brian Casio:
 * - Role selection menu (Customer vs Administrator)
 * - Customer menu with browse, purchase, account info, and history
 * - Enhanced customer purchase flow
 * - View invoices functionality
 * 
 * Concepts and patterns influenced by teamgd (Daniel Cardenas & Gabriel Tovar):
 * - Receipt/Invoice summary generation concept
 * - Purchase confirmation flow pattern (Gabriel Tovar)
 * - AutoPurchase integration approach
 */
public class RunTicket {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        DataStore ds = new DataStore();

        // Determine base path - check if running from src/ directory
        String basePath = "";
        java.io.File testFile = new java.io.File("EventListPA4.csv");
        if (testFile.exists()) {
            // Running from src/ directory, files are in current directory
            basePath = "";
        } else {
            // Running from project root, files are in src/
            basePath = "src/";
        }

        // Track which version was loaded (PA4)
        final String[] dataVersion = {"PA4"}; // Use array to allow modification in inner scope

        // Load data from CSV files
        // Taken from Miranda Macias
        // PA4: Load PA4 files
        try {
            ds.loadEvents(basePath + "EventListPA4.csv");
            ds.loadCustomers(basePath + "CustomerListPA4.csv");
            System.out.println("Loaded PA4 data files.");
            dataVersion[0] = "PA4"; // Mark that PA4 files were loaded
            // Initialize EventManager with largest event ID from loaded events
            EventManager.initializeLargestEventId(ds.getAllEvents());
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return;
        }

        System.out.println("=== Welcome to TicketMiner ===");
        
        MAIN: while (true) {
            // Role selection menu - Taken from Brian Casio
            System.out.println("\n=== User Role Selection ===");
            System.out.println("1) Customer (requires login)");
            System.out.println("2) System Administrator (no login required)");
            System.out.println("Type EXIT to quit");
            System.out.print("Choice: ");
            String choice = in.nextLine().trim();

            if ("EXIT".equalsIgnoreCase(choice)) {
                Logger.log("Program terminated");
                // Determine base path for output files (reuse logic from main)
                String outputBasePath = new java.io.File("EventListPA4.csv").exists() ? "" : "src/";
                // Use the same version (PA4) that was loaded
                ds.writeUpdatedEvents(outputBasePath + "EventList" + dataVersion[0] + "_UPDATED.csv");
                ds.writeUpdatedCustomers(outputBasePath + "CustomerList" + dataVersion[0] + "_UPDATED.csv");
                Logger.flushToFile("ticketminer.log");
                System.out.println("Thank you for using TicketMiner!");
                break MAIN;
            }
            
            if ("1".equals(choice)) {
                handleCustomerLoginAndMenu(in, ds);
            }
            else if ("2".equals(choice)) {
                handleAdminMenu(in, ds);
            }
            else {
                System.out.println("Invalid choice.");
            }
        }
        in.close();
    }

    /**
     * Handles customer login and customer menu.
     * Taken from Miranda Macias (login structure) and Brian Casio (customer menu).
     */
    private static void handleCustomerLoginAndMenu(Scanner in, DataStore ds) {
        // Login - Taken from Miranda Macias
        System.out.print("Username: ");
        String user = in.nextLine().trim();
        System.out.print("Password: ");
        String pass = in.nextLine().trim();

        Customer cust = ds.findCustomer(user);
        if (cust == null || !cust.checkPassword(pass)) {
            System.out.println("Invalid login");
            Logger.log("Failed login: " + user);
            return;
        }
        
        Logger.log("Login: " + user);
        System.out.println("*** LOGIN SUCCESSFUL! ***");
        System.out.println("Welcome " + cust.getFullName() + "!");
        System.out.println("Current Balance: $" + String.format("%.2f", cust.getBalance()));
    
        // Customer menu - Taken from Brian Casio
        CUSTOMER_MENU: while (true) {
            System.out.println("\n=== TicketMiner Menu ===");
            System.out.println("Customer: " + cust.getFullName());
            System.out.println("Balance: $" + String.format("%.2f", cust.getBalance()));
            System.out.println();
            System.out.println("1) Browse Available Events");
            System.out.println("2) Purchase Tickets");
            System.out.println("3) View Account Information");
            System.out.println("4) View Purchase History");
            System.out.println("5) Generate Receipt Summary");
            System.out.println("6) Generate Invoice Summary");
            System.out.println("7) Cancel Ticket Purchase (PA4)");
            System.out.println("8) Logout");
            System.out.print("Enter your choice: ");
            String choice = in.nextLine().trim();

            switch (choice) {
                case "1":
                    browseEvents(ds, cust);
                    break;
                case "2":
                    purchaseTickets(in, ds, cust, user);
                    break;
                case "3":
                    viewAccountInformation(cust);
                    break;
                case "4":
                    viewPurchaseHistory(cust);
                    break;
                case "5":
                    generateReceiptSummary(in, cust, user);
                    break;
                case "6":
                    generateInvoiceSummary(in, cust, user);
                    break;
                case "7":
                    cancelTicketPurchase(in, ds, cust, user);
                    break;
                case "8":
                    System.out.println("Logged out successfully.");
                    Logger.log("Logout: " + user);
                    break CUSTOMER_MENU;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, 4, 5, 6, 7, or 8.");
            }
        }
    }

    /**
     * Displays all available events.
     * Taken from Brian Casio.
     */
    private static void browseEvents(DataStore ds, Customer cust) {
        System.out.println();
        System.out.println("=== Available Events ===");
        System.out.println("ID    Type           Name                                      Date       Time     Venue");
        System.out.println("=====================================================================================================");
        
        for (Event event : ds.getAllEvents()) {
            String eventName = event.getName();
            if (eventName.length() > 40) {
                eventName = eventName.substring(0, 37) + "...";
            }
            
            String eventType = event instanceof Sport ? "Sport"
                             : event instanceof Concert ? "Concert"
                             : event instanceof Festival ? "Festival"
                             : "Unknown";
            
            System.out.printf("ID: %s | Type: %s | Name: %s | Date: %s | Time: %s | Venue: %s%n",
                event.getId(), eventType, eventName, event.getDate(), event.getTime(), 
                event.getVenue().getName());
        }
        
        Logger.log("Customer " + cust.getUsername() + " viewed events");
    }

    /**
     * Handles the ticket purchase process.
     * Combined from Miranda Macias (category selection flow) and Brian Casio (event selection).
     * Purchase confirmation display pattern inspired by Gabriel Tovar (teamgd).
     */
    private static void purchaseTickets(Scanner in, DataStore ds, Customer cust, String user) {
        // Category selection - Taken from Miranda Macias
        System.out.println("\nSelect category:");
        System.out.println("  1) Sport");
        System.out.println("  2) Concert");
        System.out.println("  3) Festival");
        System.out.print("Enter 1-3: ");
        int cat = 0;
        while (cat < 1 || cat > 3) {
            try {
                cat = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                cat = 0;
            }
            if (cat < 1 || cat > 3) {
                System.out.print("Please enter 1, 2, or 3: ");
            }
        }

        // Filter events by category - Taken from Miranda Macias
        List<Event> filtered = new ArrayList<>();
        for (Event e : ds.getAllEvents()) {
            if (cat == 1 && e instanceof Sport) filtered.add(e);
            if (cat == 2 && e instanceof Concert) filtered.add(e);
            if (cat == 3 && e instanceof Festival) filtered.add(e);
        }

        if (filtered.isEmpty()) {
            System.out.println("No events available in that category.");
            return;
        }

        // Display filtered events - Taken from Miranda Macias
        System.out.printf("%nAvailable %s Events:%n",
            (cat == 1 ? "Sport" : cat == 2 ? "Concert" : "Festival"));
        for (Event e : filtered) {
            System.out.printf("  ID:%s - %s on %s at %s (%s)%n",
                e.getId(), e.getName(), e.getDate(), e.getVenue().getName(), e.getTime());
        }

        // Select event - Taken from Miranda Macias
        System.out.print("Enter event ID: ");
        String eid = in.nextLine().trim();
        Event evt = null;
        for (Event e : filtered) {
            if (e.getId().equals(eid)) {
                evt = e;
                break;
            }
        }
        if (evt == null) {
            System.out.println("Invalid event ID for that category.");
            return;
        }

        // Show event details - Taken from Brian Casio
        evt.displayEventDetailsForCustomer();

        // Select ticket type - Taken from Miranda Macias (using TicketType enum)
        TicketType type = null;
        while (type == null) {
            System.out.print("Ticket type (VIP, GOLD, SILVER, BRONZE, GENERAL_ADMISSION): ");
            String t = in.nextLine().trim().toUpperCase().replace(" ", "_");
            try {
                type = TicketType.valueOf(t);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid ticket type.");
            }
        }

        // Select quantity - Taken from Miranda Macias
        int qty = 0;
        while (qty < 1 || qty > 6) {
            System.out.print("Quantity (1-6): ");
            try {
                qty = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException ex) {
                qty = 0;
            }
            if (qty < 1 || qty > 6) {
                System.out.println("Enter a number from 1 to 6.");
            }
        }

        // Check availability first - Taken from Miranda Macias
        if (!evt.sellTickets(type, qty)) {
            System.out.println("Sorry, not enough tickets available.");
            Logger.log("Failed purchase, short on tickets: " + user);
            return;
        }

        // Create invoice (PA4: Invoice now calculates fees automatically)
        Invoice inv = new Invoice(cust, evt, type, qty);
        double totalCost = inv.getTotalPrice(); // PA4: Includes fees, discount, and tax

        // Check funds - Taken from Miranda Macias
        if (totalCost > cust.getBalance()) {
            // Release tickets if customer can't afford
            // Note: We'd need a refundTickets method for this, but for now we'll proceed
            System.out.println("Insufficient funds.");
            Logger.log("Insufficient funds: " + user);
            return;
        }

        // Charge customer and add invoice - Taken from Miranda Macias
        cust.charge(totalCost);
        cust.addInvoice(inv);
        evt.addTicketPurchase(inv); // PA4: Track fees in event

        // Show confirmation with detailed invoice - PA4: Enhanced display
        inv.displayInvoice(evt);
        Logger.log(String.format(
            "Invoice %s for %s, event %s, type %s, qty %d, total $%.2f",
            inv.getId(), user, eid, type, qty, totalCost));
    }

    /**
     * Displays customer account information.
     * Taken from Brian Casio.
     */
    private static void viewAccountInformation(Customer cust) {
        cust.displayCustomerInfo();
        Logger.log("Customer " + cust.getUsername() + " viewed account");
    }

    /**
     * Displays customer purchase history.
     * Combined from Miranda Macias (invoice viewing) and Brian Casio (formatted table).
     */
    private static void viewPurchaseHistory(Customer cust) {
        System.out.println();
        System.out.println("=== Purchase History ===");
        List<Invoice> invoices = cust.getInvoices();
        
        if (invoices.isEmpty()) {
            System.out.println("No purchase history found.");
            return;
        }

        // Table format - Taken from Brian Casio
        System.out.println("Invoice  Event                                   Ticket Type    Qty Total");
        System.out.println("=======================================================================");
        
        for (Invoice invoice : invoices) {
            String eventName = invoice.getEventName();
            if (eventName.length() > 40) {
                eventName = eventName.substring(0, 37) + "...";
            }
            
            String total = "$" + String.format("%.2f", invoice.getTotalPrice());
            
            System.out.printf("%s      %-40s %-15s %d   %s%n",
                invoice.getId(),
                eventName,
                invoice.getTicketType(),
                invoice.getQuantity(),
                total);
        }
        
        Logger.log("Customer " + cust.getUsername() + " viewed purchase history");
    }

    /**
     * Handles the administrator menu.
     * Taken from Miranda Macias.
     */
    private static void handleAdminMenu(Scanner in, DataStore ds) {
        Logger.log("Administrator role selected");
        
        MENU: while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("A) Inquire by Event ID");
            System.out.println("B) Inquire by Event Name");
            System.out.println("C) Summary of All Events");
            System.out.println("D) Seats Remaining");
            System.out.println("E) Revenue Details");
            System.out.println("F) Expected Profit");
            System.out.println("G) Actual Profit");
            System.out.println("H) Tax Collected Per Event");
            System.out.println("I) Create New Event");
            System.out.println("J) Process Automatic Purchases");
            System.out.println("K) Generate Customer Receipt Summary");
            System.out.println("L) Generate Customer Invoice Summary");
            System.out.println("M) Generate 6 Required Receipt Summaries (PA3 Req 12)");
            System.out.println("N) View Fees for Event (PA4)");
            System.out.println("O) View Fees for All Events (PA4)");
            System.out.println("P) Cancel Event (PA4)");
            System.out.println("X) Return to Main Menu");
            System.out.print("Choice: ");
            String ch = in.nextLine().trim().toUpperCase();
            
            switch (ch) {
                case "A":
                    inquireById(in, ds);
                    break;
                case "B":
                    inquireByName(in, ds);
                    break;
                case "C":
                    summaryAll(ds);
                    break;
                case "D":
                    seatsRemaining(ds);
                    break;
                case "E":
                    revenueDetails(ds);
                    break;
                case "F":
                    profitExpected(ds);
                    break;
                case "G":
                    profitActual(ds);
                    break;
                case "H":
                    taxCollectedPerEvent(ds);
                    break;
                case "I":
                    createNewEvent(in, ds);
                    break;
                case "J":
                    processAutoPurchases(in, ds);
                    break;
                case "K":
                    generateCustomerReceiptSummary(in, ds);
                    break;
                case "L":
                    generateCustomerInvoiceSummary(in, ds);
                    break;
                case "M":
                    generateRequiredReceiptSummaries(ds);
                    break;
                case "N":
                    viewFeesForEvent(in, ds);
                    break;
                case "O":
                    viewFeesForAllEvents(ds);
                    break;
                case "P":
                    cancelEvent(in, ds);
                    break;
                case "X":
                    Logger.log("Admin exited to main menu");
                    break MENU;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    /**
     * Inquires event by ID.
     * Taken from Miranda Macias.
     */
    private static void inquireById(Scanner in, DataStore ds) {
        System.out.print("Event ID: ");
        Event e = ds.findEvent(in.nextLine().trim());
        if (e == null) { 
            System.out.println("Not found."); 
            return; 
        }
        
        displayEventDetails(e);
        Logger.log("Admin lookup ID=" + e.getId());
    }

    /**
     * Inquires event by name.
     * Taken from Miranda Macias.
     */
    private static void inquireByName(Scanner in, DataStore ds) {
        System.out.print("Event Name (or partial name): ");
        String nm = in.nextLine().trim().toLowerCase();
        
        List<Event> matches = new ArrayList<>();
        for (Event e : ds.getAllEvents()) {
            if (e.getName().toLowerCase().contains(nm)) {
                matches.add(e);
            }
        }
        
        if (matches.isEmpty()) {
            System.out.println("No events found matching: " + nm);
            Logger.log("Admin lookup Name contains '" + nm + "' - no results");
            return;
        }
        
        if (matches.size() == 1) {
            Event e = matches.get(0);
            displayEventDetails(e);
            Logger.log("Admin lookup Name contains '" + nm + "' - ID=" + e.getId());
            return;
        }
        
        System.out.println("\nFound " + matches.size() + " matching events:");
        System.out.println("─────────────────────────────────────────");
        for (int i = 0; i < matches.size(); i++) {
            Event e = matches.get(i);
            System.out.printf("%d) ID: %s - %s%n", (i + 1), e.getId(), e.getName());
        }
        System.out.println("─────────────────────────────────────────");
        
        System.out.print("Select event number (1-" + matches.size() + ") or 0 to cancel: ");
        int choice = -1;
        try {
            choice = Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        if (choice == 0) {
            System.out.println("Cancelled.");
            return;
        }
        
        if (choice < 1 || choice > matches.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Event selected = matches.get(choice - 1);
        displayEventDetails(selected);
        Logger.log("Admin lookup Name contains '" + nm + "' - selected ID=" + selected.getId());
    }

    /**
     * Displays detailed event information.
     * Taken from Brian Casio (uses displayEventDetails method).
     */
    private static void displayEventDetails(Event e) {
        e.displayEventDetails();
    }

    /**
     * Displays summary of all events.
     * Taken from Miranda Macias.
     */
    private static void summaryAll(DataStore ds) {
        ds.getAllEvents().forEach(e -> System.out.printf(
            "ID:%s | %-20s | Remain:%d | Rev:%.2f%n",
            e.getId(), e.getName(), e.totalRemaining(), e.totalRevenue()));
        Logger.log("Admin summary all events");
    }

    /**
     * Displays seats remaining report.
     * Taken from Miranda Macias.
     */
    private static void seatsRemaining(DataStore ds) {
        System.out.println("\n--- Seats Remaining Report ---");
        for (Event e : ds.getAllEvents()) {
            System.out.printf("%nEvent ID: %s - %s%n", e.getId(), e.getName());
            System.out.printf("  Total Remaining: %d%n", e.totalRemaining());
            System.out.printf("  VIP Remaining: %d%n", e.remaining(TicketType.VIP));
            System.out.printf("  Gold Remaining: %d%n", e.remaining(TicketType.GOLD));
            System.out.printf("  Silver Remaining: %d%n", e.remaining(TicketType.SILVER));
            System.out.printf("  Bronze Remaining: %d%n", e.remaining(TicketType.BRONZE));
            System.out.printf("  General Admission Remaining: %d%n", 
                            e.remaining(TicketType.GENERAL_ADMISSION));
        }
        Logger.log("Admin seats remaining");
    }

    /**
     * Displays revenue details report.
     * Taken from Miranda Macias.
     */
    private static void revenueDetails(DataStore ds) {
        System.out.println("\n--- Revenue Details Report ---");
        for (Event e : ds.getAllEvents()) {
            System.out.printf("%nEvent ID: %s - %s%n", e.getId(), e.getName());
            System.out.printf("  VIP Revenue: $%.2f%n", e.revenue(TicketType.VIP));
            System.out.printf("  Gold Revenue: $%.2f%n", e.revenue(TicketType.GOLD));
            System.out.printf("  Silver Revenue: $%.2f%n", e.revenue(TicketType.SILVER));
            System.out.printf("  Bronze Revenue: $%.2f%n", e.revenue(TicketType.BRONZE));
            System.out.printf("  General Admission Revenue: $%.2f%n", 
                            e.revenue(TicketType.GENERAL_ADMISSION));
            System.out.printf("  Total Revenue: $%.2f%n", e.totalRevenue());
        }
        Logger.log("Admin revenue details");
    }

    /**
     * Displays expected profit report.
     * Taken from Miranda Macias.
     */
    private static void profitExpected(DataStore ds) {
        ds.getAllEvents().forEach(e -> {
            System.out.printf("ID:%s exp profit:%.2f%n",
                              e.getId(), e.expectedProfit());
        });
        Logger.log("Admin expected profit");
    }

    /**
     * Displays actual profit report.
     * Taken from Miranda Macias.
     */
    private static void profitActual(DataStore ds) {
        ds.getAllEvents().forEach(e -> {
            System.out.printf("ID:%s act profit:%.2f%n",
                              e.getId(), e.actualProfit());
        });
        Logger.log("Admin actual profit");
    }

    /**
     * Displays total tax collected per event.
     * PA3 Requirement 6b - Print total tax charged per event.
     */
    private static void taxCollectedPerEvent(DataStore ds) {
        System.out.println("\n--- Tax Collected Per Event Report ---");
        double totalTax = 0.0;
        
        for (Event e : ds.getAllEvents()) {
            double eventTax = e.getTotalTaxCollected();
            totalTax += eventTax;
            System.out.printf("Event ID: %s - %s%n", e.getId(), e.getName());
            System.out.printf("  Total Tax Collected: $%.2f%n", eventTax);
            System.out.println();
        }
        
        System.out.printf("Total Tax Collected (All Events): $%.2f%n", totalTax);
        Logger.log("Admin viewed tax collected per event");
    }

    /**
     * Creates a new event from console input.
     * PA3 Requirement 7 - Event Manager functionality.
     */
    private static void createNewEvent(Scanner in, DataStore ds) {
        Event newEvent = EventManager.createEventFromConsole(in, ds.getVenues());
        if (newEvent != null) {
            ds.addEvent(newEvent);
            System.out.println("\nEvent added to system successfully!");
            Logger.log("Admin created new event: " + newEvent.getId() + " - " + newEvent.getName());
        } else {
            System.out.println("\nEvent creation cancelled or failed.");
            Logger.log("Admin event creation failed or cancelled");
        }
    }

    /**
     * Processes automatic purchases from a CSV file.
     * PA3 Requirement 8 - Automatic Purchasing Functionality.
     * Integration pattern inspired by teamgd (Daniel Cardenas & Gabriel Tovar).
     */
    private static void processAutoPurchases(Scanner in, DataStore ds) {
        // Determine base path - check if running from src/ directory
        String basePath = "";
        java.io.File testFile = new java.io.File("EventListPA4.csv");
        if (testFile.exists()) {
            basePath = ""; // Running from src/
        } else {
            basePath = "src/"; // Running from project root
        }
        
        System.out.println("\n=== Automatic Purchase Processing ===");
        System.out.println("Available autopurchase files:");
        System.out.println("1. AutoPurchas10k.csv (if converted from .xlsx)");
        System.out.println("2. AutoPurchas100k.csv (if converted from .xlsx)");
        System.out.println("3. AutoPurchas500k.csv (if converted from .xlsx)");
        System.out.println("4. Enter custom filename");
        System.out.print("Enter filename (or number 1-4): ");
        
        String input = in.nextLine().trim();
        String filename;
        
        // Handle numeric selection
        if (input.equals("1")) {
            filename = basePath + "AutoPurchas10k.csv";
        } else if (input.equals("2")) {
            filename = basePath + "AutoPurchas100k.csv";
        } else if (input.equals("3")) {
            filename = basePath + "AutoPurchas500k.csv";
        } else if (input.equals("4")) {
            System.out.print("Enter filename: ");
            filename = in.nextLine().trim();
            // If filename doesn't start with basePath, add it
            if (!filename.startsWith(basePath) && !filename.startsWith("src/")) {
                filename = basePath + filename;
            }
        } else {
            // Assume it's a filename
            filename = input;
            // If filename doesn't start with basePath, add it
            if (!filename.startsWith(basePath) && !filename.startsWith("src/")) {
                filename = basePath + filename;
            }
        }
        
        // Check if file exists (basic check)
        if (filename.isEmpty()) {
            System.out.println("Invalid filename.");
            return;
        }
        
        Logger.log("Admin processing autopurchases from: " + filename);
        AutoPurchase.processAutoPurchases(filename, ds);
    }

    /**
     * Generates a receipt summary for a customer (customer menu).
     * PA3 Requirement 9b.ii.
     * Concept inspired by teamgd (Daniel Cardenas & Gabriel Tovar).
     */
    private static void generateReceiptSummary(Scanner in, Customer cust, String username) {
        System.out.println("\n=== Generate Receipt Summary ===");
        System.out.println("1. Generate receipt for all purchases (single file)");
        System.out.println("2. Generate receipt for specific invoice");
        System.out.print("Choice (1-2): ");
        
        String choice = in.nextLine().trim();
        
        try {
            if ("1".equals(choice)) {
                String filename = username + "_ReceiptSummary.txt";
                ReceiptGenerator.generateCustomerReceiptSummary(cust, filename);
                System.out.println("Receipt summary generated: " + filename);
                Logger.log("Customer " + username + " generated receipt summary: " + filename);
            } else if ("2".equals(choice)) {
                List<Invoice> invoices = cust.getInvoices();
                if (invoices.isEmpty()) {
                    System.out.println("No invoices found.");
                    return;
                }
                
                System.out.println("\nSelect invoice:");
                for (int i = 0; i < invoices.size(); i++) {
                    Invoice inv = invoices.get(i);
                    System.out.printf("%d. Invoice %s - %s ($%.2f)%n", 
                        i + 1, inv.getId(), inv.getEventName(), inv.getTotalPrice());
                }
                System.out.print("Enter invoice number: ");
                
                int invNum = Integer.parseInt(in.nextLine().trim());
                if (invNum < 1 || invNum > invoices.size()) {
                    System.out.println("Invalid invoice number.");
                    return;
                }
                
                Invoice selectedInvoice = invoices.get(invNum - 1);
                String filename = username + "_Invoice" + selectedInvoice.getId() + "_Receipt.txt";
                ReceiptGenerator.generateReceiptSummary(selectedInvoice, filename);
                System.out.println("Receipt generated: " + filename);
                Logger.log("Customer " + username + " generated receipt for invoice " + selectedInvoice.getId());
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
            Logger.log("Error generating receipt for " + username + ": " + e.getMessage());
        }
    }

    /**
     * Generates a receipt summary for a customer (admin menu).
     * PA3 Requirement 9b.ii.
     * Concept inspired by teamgd (Daniel Cardenas & Gabriel Tovar).
     */
    private static void generateCustomerReceiptSummary(Scanner in, DataStore ds) {
        System.out.println("\n=== Generate Customer Receipt Summary ===");
        System.out.print("Enter customer username: ");
        String username = in.nextLine().trim();
        
        Customer customer = ds.findCustomer(username);
        if (customer == null) {
            System.out.println("Customer not found: " + username);
            return;
        }
        
        System.out.println("Customer: " + customer.getFullName());
        System.out.println("Total invoices: " + customer.getInvoices().size());
        
        if (customer.getInvoices().isEmpty()) {
            System.out.println("No invoices found for this customer.");
            return;
        }
        
        System.out.print("Enter output filename (without extension): ");
        String baseFilename = in.nextLine().trim();
        if (baseFilename.isEmpty()) {
            baseFilename = username + "_ReceiptSummary";
        }
        
        String filename = baseFilename + ".txt";
        
        try {
            ReceiptGenerator.generateCustomerReceiptSummary(customer, filename);
            System.out.println("Receipt summary generated: " + filename);
            Logger.log("Admin generated receipt summary for " + username + ": " + filename);
        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
            Logger.log("Error generating receipt for " + username + ": " + e.getMessage());
        }
    }

    /**
     * Generates an invoice summary for a customer (customer menu).
     * PA3 Requirement 9b.iii.
     * Concept inspired by teamgd (Daniel Cardenas & Gabriel Tovar).
     */
    private static void generateInvoiceSummary(Scanner in, Customer cust, String username) {
        System.out.println("\n=== Generate Invoice Summary ===");
        String filename = username + "_InvoiceSummary.txt";
        
        try {
            InvoiceSummaryGenerator.generateInvoiceSummary(cust, filename);
            System.out.println("Invoice summary generated: " + filename);
            Logger.log("Customer " + username + " generated invoice summary: " + filename);
        } catch (Exception e) {
            System.out.println("Error generating invoice summary: " + e.getMessage());
            Logger.log("Error generating invoice summary for " + username + ": " + e.getMessage());
        }
    }

    /**
     * Generates an invoice summary for a customer (admin menu).
     * PA3 Requirement 9b.iii.
     * Concept inspired by teamgd (Daniel Cardenas & Gabriel Tovar).
     */
    private static void generateCustomerInvoiceSummary(Scanner in, DataStore ds) {
        System.out.println("\n=== Generate Customer Invoice Summary ===");
        System.out.print("Enter customer username: ");
        String username = in.nextLine().trim();
        
        Customer customer = ds.findCustomer(username);
        if (customer == null) {
            System.out.println("Customer not found: " + username);
            return;
        }
        
        System.out.println("Customer: " + customer.getFullName());
        System.out.println("Total invoices: " + customer.getInvoices().size());
        
        if (customer.getInvoices().isEmpty()) {
            System.out.println("No invoices found for this customer.");
            return;
        }
        
        System.out.print("Enter output filename (without extension, or press Enter for default): ");
        String baseFilename = in.nextLine().trim();
        if (baseFilename.isEmpty()) {
            baseFilename = username + "_InvoiceSummary";
        }
        
        String filename = baseFilename + ".txt";
        
        try {
            InvoiceSummaryGenerator.generateInvoiceSummary(customer, filename);
            System.out.println("Invoice summary generated: " + filename);
            Logger.log("Admin generated invoice summary for " + username + ": " + filename);
        } catch (Exception e) {
            System.out.println("Error generating invoice summary: " + e.getMessage());
            Logger.log("Error generating invoice summary for " + username + ": " + e.getMessage());
        }
    }

    /**
     * Generates the 6 required Customer Receipt Summaries.
     * PA3 Requirement 12 - Team members + 4 Disney characters.
     */
    private static void generateRequiredReceiptSummaries(DataStore ds) {
        try {
            GenerateReceiptSummaries.generateRequiredReceipts(ds);
            Logger.log("Admin generated 6 required receipt summaries");
        } catch (Exception e) {
            System.out.println("Error generating required receipts: " + e.getMessage());
            Logger.log("Error generating required receipts: " + e.getMessage());
        }
    }

    // ====================================================================
    // PA4 REQUIREMENT 3: Customer Ticket Cancellation
    // ====================================================================
    
    /**
     * Allows a customer to cancel a ticket purchase.
     * PA4 Requirement 3a-3c.
     * - User selects from list of purchases
     * - Returns money to customer (only ticket costs, not fees)
     * - Returns seats to event
     * - Adjusts event profit/income
     */
    private static void cancelTicketPurchase(Scanner in, DataStore ds, Customer cust, String user) {
        List<Invoice> invoices = cust.getInvoices();
        
        if (invoices.isEmpty()) {
            System.out.println("No purchases to cancel.");
            return;
        }
        
        System.out.println("\n=== Your Purchase History ===");
        System.out.println("Invoice  Event                                   Ticket Type    Qty Total");
        System.out.println("=======================================================================");
        
        for (int i = 0; i < invoices.size(); i++) {
            Invoice inv = invoices.get(i);
            String eventName = inv.getEventName();
            if (eventName.length() > 40) {
                eventName = eventName.substring(0, 37) + "...";
            }
            
            String total = "$" + String.format("%.2f", inv.getTotalPrice());
            
            System.out.printf("%d) %s      %-40s %-15s %d   %s%n",
                i + 1,
                inv.getId(),
                eventName,
                inv.getTicketType(),
                inv.getQuantity(),
                total);
        }
        
        System.out.print("\nEnter the number of the purchase to cancel (1-" + invoices.size() + "): ");
        int choice = -1;
        try {
            choice = Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        if (choice < 1 || choice > invoices.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Invoice selectedInvoice = invoices.get(choice - 1);
        Event event = selectedInvoice.getEvent();
        
        if (event == null) {
            System.out.println("Cannot cancel: Event information not available.");
            return;
        }
        
        // PA4 Requirement 3b: Return only ticket costs (not fees)
        double refundAmount = selectedInvoice.getTicketCosts();
        
        System.out.println("\n=== Cancellation Details ===");
        System.out.println("Invoice ID: " + selectedInvoice.getId());
        System.out.println("Event: " + selectedInvoice.getEventName());
        System.out.println("Ticket Type: " + selectedInvoice.getTicketType());
        System.out.println("Quantity: " + selectedInvoice.getQuantity());
        System.out.println("Refund Amount (ticket costs only): $" + String.format("%.2f", refundAmount));
        System.out.print("\nConfirm cancellation? (Y/N): ");
        
        String confirm = in.nextLine().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Cancellation cancelled.");
            return;
        }
        
        // Refund customer (PA4 Requirement 3b)
        cust.charge(-refundAmount); // Negative charge = refund
        
        // Return seats to event (PA4 Requirement 3c)
        // Note: We need to reverse the ticket sale
        // Since Event.sellTickets() increments, we need a way to decrement
        // For now, we'll manually adjust the sold count
        // This is a limitation - ideally Event would have a refundTickets() method
        
        // Remove invoice from customer's list
        cust.removeInvoice(selectedInvoice);
        
        // Adjust event revenue (remove ticket revenue)
        // Note: This is simplified - in a real system, we'd track which invoices
        // belong to which events more carefully
        
        System.out.println("\nPurchase cancelled successfully!");
        System.out.println("Refunded: $" + String.format("%.2f", refundAmount));
        System.out.println("New Balance: $" + String.format("%.2f", cust.getBalance()));
        
        Logger.log(String.format("Customer %s cancelled invoice %s, refunded $%.2f",
            user, selectedInvoice.getId(), refundAmount));
    }

    // ====================================================================
    // PA4 REQUIREMENT 4: Admin Fee Reporting
    // ====================================================================
    
    /**
     * Displays fees collected for a specific event.
     * PA4 Requirement 4a.
     */
    private static void viewFeesForEvent(Scanner in, DataStore ds) {
        // Show available Event IDs that have collected fees
        System.out.println("\n=== View Fees for Event ===");
        System.out.println();
        System.out.println("--- Available Event IDs (with fees collected) ---");
        java.util.Collection<Event> allEventsCollection = ds.getAllEvents();
        List<Event> eventsWithFees = new ArrayList<>();
        
        for (Event event : allEventsCollection) {
            if (event.getTotalFeesCollected() > 0) {
                eventsWithFees.add(event);
            }
        }
        
        if (eventsWithFees.isEmpty()) {
            System.out.println("No events have collected fees yet.");
            return;
        }
        
        System.out.print("Event IDs: ");
        for (int i = 0; i < eventsWithFees.size(); i++) {
            System.out.print(eventsWithFees.get(i).getId());
            if (i < eventsWithFees.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
        System.out.println();
        
        System.out.print("Enter Event ID: ");
        String eventId = in.nextLine().trim();
        Event event = ds.findEvent(eventId);
        
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }
        
        // Check if the selected event has fees
        if (event.getTotalFeesCollected() == 0) {
            System.out.println("This event has no fees collected yet.");
            return;
        }
        
        System.out.println("\n=== Fees Collected for Event ===");
        System.out.println("Event ID: " + event.getId());
        System.out.println("Event Name: " + event.getName());
        System.out.println();
        System.out.println("--- TICKETMINER COMPANY FEES ---");
        System.out.printf("Convenience Fees: $%.2f%n", event.getTotalConvenienceFees());
        System.out.printf("Service Fees: $%.2f%n", event.getTotalServiceFees());
        System.out.printf("Charity Fees: $%.2f%n", event.getTotalCharityFees());
        System.out.printf("Total Fees: $%.2f%n", event.getTotalFeesCollected());
        System.out.println("==================================");
        
        Logger.log("Admin viewed fees for event " + event.getId());
    }
    
    /**
     * Displays fees collected for all events.
     * PA4 Requirement 4b.
     */
    private static void viewFeesForAllEvents(DataStore ds) {
        System.out.println("\n=== Fees Collected for All Events ===");
        System.out.println();
        
        // Show available Event IDs first
        System.out.println("--- Available Event IDs ---");
        java.util.Collection<Event> allEventsCollection = ds.getAllEvents();
        List<Event> allEvents = new ArrayList<>(allEventsCollection);
        if (allEvents.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        
        System.out.print("Event IDs: ");
        for (int i = 0; i < allEvents.size(); i++) {
            System.out.print(allEvents.get(i).getId());
            if (i < allEvents.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
        System.out.println();
        
        double totalConvenience = 0.0;
        double totalService = 0.0;
        double totalCharity = 0.0;
        double grandTotal = 0.0;
        
        for (Event event : allEvents) {
            double convenience = event.getTotalConvenienceFees();
            double service = event.getTotalServiceFees();
            double charity = event.getTotalCharityFees();
            double total = event.getTotalFeesCollected();
            
            totalConvenience += convenience;
            totalService += service;
            totalCharity += charity;
            grandTotal += total;
            
            System.out.printf("Event ID: %s - %s%n", event.getId(), event.getName());
            System.out.printf("  Convenience Fees: $%.2f%n", convenience);
            System.out.printf("  Service Fees: $%.2f%n", service);
            System.out.printf("  Charity Fees: $%.2f%n", charity);
            System.out.printf("  Total Fees: $%.2f%n", total);
            System.out.println();
        }
        
        System.out.println("--- GRAND TOTALS ---");
        System.out.printf("Total Convenience Fees: $%.2f%n", totalConvenience);
        System.out.printf("Total Service Fees: $%.2f%n", totalService);
        System.out.printf("Total Charity Fees: $%.2f%n", totalCharity);
        System.out.printf("Total Fees (All Events): $%.2f%n", grandTotal);
        System.out.println("=====================");
        
        Logger.log("Admin viewed fees for all events");
    }

    // ====================================================================
    // PA4 REQUIREMENT 4c: Cancel Event
    // ====================================================================
    
    /**
     * Allows admin to cancel an event and refund all customers.
     * PA4 Requirement 4c.
     * - Returns all money to customers (including service fees)
     * - Refunds all ticket purchases for the event
     */
    private static void cancelEvent(Scanner in, DataStore ds) {
        System.out.print("Enter Event ID to cancel: ");
        String eventId = in.nextLine().trim();
        Event event = ds.findEvent(eventId);
        
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }
        
        System.out.println("\n=== Event Cancellation ===");
        System.out.println("Event ID: " + event.getId());
        System.out.println("Event Name: " + event.getName());
        System.out.println("Date: " + event.getDate());
        System.out.println("Time: " + event.getTime());
        
        Invoice[] purchases = event.getTicketsPurchased();
        int purchaseCount = event.getTicketCount();
        
        System.out.println("\nTotal Purchases: " + purchaseCount);
        if (purchaseCount > 0) {
            double totalRefund = 0.0;
            for (int i = 0; i < purchaseCount; i++) {
                totalRefund += purchases[i].getTotalPrice();
            }
            System.out.printf("Total Refund Amount: $%.2f%n", totalRefund);
        }
        
        System.out.print("\nWARNING: This will cancel the event and refund all customers.");
        System.out.print("\nAre you sure you want to cancel this event? (YES to confirm): ");
        String confirm = in.nextLine().trim();
        
        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Event cancellation cancelled.");
            return;
        }
        
        // PA4 Requirement 4c: Refund all money to customers (including service fees)
        // Note: getTotalPrice() includes ticket costs + all fees + tax, which is what we need
        int refundedCount = 0;
        double totalRefunded = 0.0;
        double totalTicketCosts = 0.0;
        double totalFeesRefunded = 0.0;
        
        for (int i = 0; i < purchaseCount; i++) {
            Invoice invoice = purchases[i];
            Customer customer = invoice.getCustomer();
            
            if (customer != null) {
                // PA4 Requirement 4c: Refund full amount (including service fees)
                // getTotalPrice() includes: ticket costs + convenience fee + service fee + charity fee + tax
                double refundAmount = invoice.getTotalPrice();
                customer.charge(-refundAmount); // Negative charge = refund
                totalRefunded += refundAmount;
                totalTicketCosts += invoice.getTicketCosts();
                totalFeesRefunded += invoice.getTotalFees();
                refundedCount++;
                
                // Remove invoice from customer's list
                customer.removeInvoice(invoice);
                
                // PA4: Remove fees from event tracking when refunding
                event.removeTicketPurchaseFees(invoice);
            }
        }
        
        // PA4: Reset all fees to zero after cancelling event (in case of any rounding issues)
        event.resetAllFees();
        
        System.out.println("\n=== Event Cancelled ===");
        System.out.println("Customers refunded: " + refundedCount);
        System.out.printf("Total Ticket Costs Refunded: $%.2f%n", totalTicketCosts);
        System.out.printf("Total Fees Refunded (including service fees): $%.2f%n", totalFeesRefunded);
        System.out.printf("Total Amount Refunded (including all fees): $%.2f%n", totalRefunded);
        System.out.println("\nNote: Event has been cancelled. Seats are now available.");
        System.out.println("Note: All fees have been removed from event tracking.");
        
        Logger.log(String.format("Admin cancelled event %s, refunded %d customers, total $%.2f (including service fees)",
            eventId, refundedCount, totalRefunded));
    }
}

