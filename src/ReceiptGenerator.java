import java.io.PrintWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates Customer Receipt Summary files.
 * PA3 Requirement 9b.ii and 12.
 * 
 * Authored for PA3
 */
public class ReceiptGenerator {
    
    /**
     * Generates a confirmation number based on purchase date.
     * Format: YYYYMMDD-HHMMSS-INV{id}
     * 
     * @param invoice The invoice to generate confirmation for
     * @return Confirmation number string
     */
    public static String generateConfirmationNumber(Invoice invoice) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        
        String datePart = invoice.getTimeStamp().format(dateFormatter);
        String timePart = invoice.getTimeStamp().format(timeFormatter);
        String invPart = "INV" + invoice.getId();
        
        return datePart + "-" + timePart + "-" + invPart;
    }
    
    /**
     * Gets event type as string from Event object.
     */
    private static String getEventType(Event event) {
        if (event instanceof Sport) {
            return "Sport";
        } else if (event instanceof Concert) {
            return "Concert";
        } else if (event instanceof Festival) {
            return "Festival";
        } else {
            return "Unknown";
        }
    }
    
    /**
     * Generates a Customer Receipt Summary file for a single invoice.
     * PA3 Requirement 9b.ii.
     * 
     * @param invoice The invoice to generate receipt for
     * @param filename The output filename
     * @throws IOException if file cannot be written
     */
    public static void generateReceiptSummary(Invoice invoice, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            Event event = invoice.getEvent();
            Customer customer = invoice.getCustomer();
            
            // Header
            writer.println("========================================");
            writer.println("    TICKETMINER RECEIPT SUMMARY");
            writer.println("========================================");
            writer.println();
            
            // Customer Information
            if (customer != null) {
                writer.println("Customer: " + invoice.getCustomerName());
            } else {
                writer.println("Customer: " + invoice.getCustomerName());
            }
            writer.println();
            
            // Purchase Details
            writer.println("Purchase Details:");
            writer.println("----------------");
            
            if (event != null) {
                writer.println("Event Type: " + getEventType(event));
                writer.println("Event Name: " + invoice.getEventName());
                writer.println("Event Date: " + event.getDate());
            } else {
                writer.println("Event Type: N/A");
                writer.println("Event Name: " + invoice.getEventName());
                writer.println("Event Date: N/A");
            }
            
            writer.println("Ticket Type: " + invoice.getTicketType());
            writer.println("Number of Tickets: " + invoice.getQuantity());
            writer.println("Total Price: $" + String.format("%.2f", invoice.getTotalPrice()));
            writer.println();
            
            // Confirmation Number (based on purchase date)
            String confirmationNumber = generateConfirmationNumber(invoice);
            writer.println("Confirmation Number: " + confirmationNumber);
            writer.println();
            
            // Footer
            writer.println("========================================");
            writer.println("Thank you for using TicketMiner!");
            writer.println("========================================");
        }
    }
    
    /**
     * Generates Customer Receipt Summary files for all invoices of a customer.
     * Creates one file per invoice.
     * PA3 Requirement 9b.ii.
     * 
     * @param customer The customer to generate receipts for
     * @param baseFilename Base filename (will append invoice ID)
     * @return Number of receipts generated
     * @throws IOException if files cannot be written
     */
    public static int generateAllReceiptsForCustomer(Customer customer, String baseFilename) throws IOException {
        List<Invoice> invoices = customer.getInvoices();
        int count = 0;
        
        for (Invoice invoice : invoices) {
            String filename = baseFilename + "_Invoice" + invoice.getId() + ".txt";
            generateReceiptSummary(invoice, filename);
            count++;
        }
        
        return count;
    }
    
    /**
     * Generates a single comprehensive Customer Receipt Summary for a customer.
     * Includes all purchases in one file.
     * PA3 Requirement 9b.ii.
     * 
     * @param customer The customer to generate receipt for
     * @param filename The output filename
     * @throws IOException if file cannot be written
     */
    public static void generateCustomerReceiptSummary(Customer customer, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            List<Invoice> invoices = customer.getInvoices();
            
            // Header
            writer.println("========================================");
            writer.println("    TICKETMINER RECEIPT SUMMARY");
            writer.println("========================================");
            writer.println();
            writer.println("Customer: " + customer.getFullName());
            writer.println("Total Purchases: " + invoices.size());
            writer.println();
            
            if (invoices.isEmpty()) {
                writer.println("No purchases found.");
                writer.println("========================================");
                return;
            }
            
            // List all purchases
            writer.println("Purchase History:");
            writer.println("----------------");
            writer.println();
            
            for (int i = 0; i < invoices.size(); i++) {
                Invoice invoice = invoices.get(i);
                Event event = invoice.getEvent();
                
                writer.println("Purchase #" + (i + 1) + ":");
                if (event != null) {
                    writer.println("  Event Type: " + getEventType(event));
                    writer.println("  Event Name: " + invoice.getEventName());
                    writer.println("  Event Date: " + event.getDate());
                } else {
                    writer.println("  Event Type: N/A");
                    writer.println("  Event Name: " + invoice.getEventName());
                    writer.println("  Event Date: N/A");
                }
                writer.println("  Ticket Type: " + invoice.getTicketType());
                writer.println("  Number of Tickets: " + invoice.getQuantity());
                writer.println("  Total Price: $" + String.format("%.2f", invoice.getTotalPrice()));
                
                String confirmationNumber = generateConfirmationNumber(invoice);
                writer.println("  Confirmation Number: " + confirmationNumber);
                writer.println();
            }
            
            // Footer
            writer.println("========================================");
            writer.println("Thank you for using TicketMiner!");
            writer.println("========================================");
        }
    }
}



