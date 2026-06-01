/**
 * The University of Texas at El Paso
 * Department of Computer Science
 * CS 3331 – Advanced Object-Oriented Programming
 * Instructor: Daniel Mejia
 * Fall 2025
 * Programming Assignment 4
 * 
 * Name: Miranda Macias, Brian Casio (teammb)
 * 
 * Lab Description: This program generates electronic tickets for 8 specific customers in a simple
 * text file format. The tickets include event information, customer details, ticket numbers, and
 * confirmation codes. This is part of the TicketMiner ticket purchasing system.
 * 
 * Honesty Statement: We declare that this submission is the result of our own work and has
 * been completed in collaboration with our team members. We have not given or received
 * unauthorized assistance on this assignment.
 */
import java.io.PrintWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates Electronic Tickets for 8 specific customers.
 * Simple text file format for entry tickets.
 */
public class ElectronicTicketGenerator {
    
    /**
     * Generates a confirmation number based on purchase date.
     */
    private static String generateConfirmationNumber(Invoice invoice) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        
        String datePart = invoice.getTimeStamp().format(dateFormatter);
        String timePart = invoice.getTimeStamp().format(timeFormatter);
        String invPart = "INV" + invoice.getId();
        
        return datePart + "-" + timePart + "-" + invPart;
    }
    
    /**
     * Gets event type as string.
     */
    private static String getEventType(Event event) {
        if (event instanceof Sport) return "Sport";
        if (event instanceof Concert) return "Concert";
        if (event instanceof Festival) return "Festival";
        return "Unknown";
    }
    
    /**
     * Generates a simple electronic ticket file.
     */
    private static void generateTicketFile(Invoice invoice, int ticketIndex, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            Event event = invoice.getEvent();
            Customer customer = invoice.getCustomer();
            
            String ticketNumber = "TKT-" + invoice.getId() + "-" + String.format("%03d", ticketIndex + 1);
            String confirmation = generateConfirmationNumber(invoice);
            
            writer.println("========================================");
            writer.println("       ELECTRONIC TICKET");
            writer.println("         TICKETMINER");
            writer.println("========================================");
            writer.println();
            writer.println("Ticket Number: " + ticketNumber);
            writer.println("Confirmation: " + confirmation);
            writer.println();
            writer.println("----------------------------------------");
            writer.println("EVENT INFORMATION:");
            if (event != null) {
                writer.println("  Type:  " + getEventType(event));
                writer.println("  Name:  " + invoice.getEventName());
                writer.println("  Date:  " + event.getDate());
                writer.println("  Time:  " + event.getTime());
                writer.println("  Venue: " + event.getVenue().getName());
            } else {
                writer.println("  Name:  " + invoice.getEventName());
            }
            writer.println();
            writer.println("TICKET DETAILS:");
            writer.println("  Type:  " + invoice.getTicketType());
            writer.println("  Price: $" + String.format("%.2f", invoice.getUnitPrice()));
            writer.println();
            writer.println("CUSTOMER:");
            writer.println("  Name: " + invoice.getCustomerName());
            if (customer != null) {
                writer.println("  ID:   " + customer.getId());
            }
            writer.println();
            writer.println("----------------------------------------");
            writer.println("ENTRY INSTRUCTIONS:");
            writer.println("  Present this ticket at venue entrance");
            writer.println("  Valid only for date and time shown");
            writer.println("  Keep this ticket safe");
            writer.println();
            writer.println("========================================");
            writer.println("Thank you for using TicketMiner!");
            writer.println("========================================");
        }
    }
    
    /**
     * Generates electronic tickets for all purchases of a customer.
     */
    private static int generateTicketsForCustomer(Customer customer, String baseFilename) throws IOException {
        List<Invoice> invoices = customer.getInvoices();
        int count = 0;
        
        for (Invoice invoice : invoices) {
            int quantity = invoice.getQuantity();
            for (int i = 0; i < quantity; i++) {
                String ticketNum = "TKT-" + invoice.getId() + "-" + String.format("%03d", i + 1);
                String filename = baseFilename + "_" + ticketNum + ".txt";
                generateTicketFile(invoice, i, filename);
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Generates electronic tickets for 8 specific customers:
     * - Dr. Mejia
     * - Ali (Alireza Nouri)
     * - Team members: Miranda Macias, Brian Casio, Daniel Cardenas, Gabriel Tovar
     * - 2 Disney characters (Mickey Mouse, Donald Duck)
     */
    public static void generateTicketsFor8Customers(DataStore dataStore) throws IOException {
        System.out.println("\n=== Generating Electronic Tickets for 8 Customers ===");
        
        String[] usernames = {
            "mejiadaniel",        // Dr. Mejia
            "alirezanouri",       // Ali (Alireza Nouri)
            "maciasmiranda",      // Miranda Macias
            "casio irachetabrian", // Brian Casio
            "cardenasdaniel",    // Daniel Cardenas
            "tovargabriel",      // Gabriel Tovar
            "mickeymouse",       // Mickey Mouse
            "donaldduck"         // Donald Duck
        };
        
        List<String> finalUsernames = new java.util.ArrayList<>();
        
        for (String username : usernames) {
            Customer customer = dataStore.findCustomer(username);
            if (customer != null) {
                finalUsernames.add(username);
            }
        }
        
        // Try alternate Mejia entry if mejiadaniel not found
        if (!finalUsernames.contains("mejiadaniel") && !finalUsernames.contains("danielmejia")) {
            Customer mejia = dataStore.findCustomer("danielmejia");
            if (mejia != null) {
                finalUsernames.add(0, "danielmejia");
            }
        }
        
        int totalTickets = 0;
        int customersProcessed = 0;
        
        for (String username : finalUsernames) {
            Customer customer = dataStore.findCustomer(username);
            if (customer != null) {
                List<Invoice> invoices = customer.getInvoices();
                if (!invoices.isEmpty()) {
                    String baseFilename = username.replace(" ", "_") + "_Ticket";
                    int ticketsGenerated = generateTicketsForCustomer(customer, baseFilename);
                    System.out.println("Generated " + ticketsGenerated + " ticket(s) for " + customer.getFullName());
                    totalTickets += ticketsGenerated;
                    customersProcessed++;
                } else {
                    System.out.println("No purchases found for " + customer.getFullName() + " - skipping");
                }
            }
        }
        
        System.out.println("\n=== Summary ===");
        System.out.println("Customers processed: " + customersProcessed);
        System.out.println("Total tickets generated: " + totalTickets);
    }
}

