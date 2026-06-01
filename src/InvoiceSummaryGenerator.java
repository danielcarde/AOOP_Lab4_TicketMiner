import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

/**
 * Generates Customer Invoice Summary files.
 * PA3 Requirement 9b.iii.
 * 
 * Lists all ticket purchases made by the customer.
 */
public class InvoiceSummaryGenerator {
    
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
     * Generates a Customer Invoice Summary listing all ticket purchases.
     * PA3 Requirement 9b.iii.
     * 
     * @param customer The customer to generate invoice summary for
     * @param filename The output filename
     * @throws IOException if file cannot be written
     */
    public static void generateInvoiceSummary(Customer customer, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            List<Invoice> invoices = customer.getInvoices();
            
            // Header
            writer.println("========================================");
            writer.println("    TICKETMINER INVOICE SUMMARY");
            writer.println("========================================");
            writer.println();
            
            // Customer Information
            writer.println("Customer Information:");
            writer.println("-------------------");
            writer.println("Name: " + customer.getFullName());
            writer.println("Username: " + customer.getUsername());
            writer.println("Current Balance: $" + String.format("%.2f", customer.getBalance()));
            writer.println("TicketMiner Member: " + (customer.hasTicketMinerMembership() ? "Yes" : "No"));
            
            if (customer.hasTicketMinerMembership()) {
                // Calculate total savings if available
                double totalSavings = 0.0;
                for (Invoice inv : invoices) {
                    if (inv.hadMemberDiscount()) {
                        totalSavings += inv.getMemberDiscount();
                    }
                }
                if (totalSavings > 0) {
                    writer.println("Total Savings from Membership: $" + String.format("%.2f", totalSavings));
                }
            }
            writer.println();
            
            // Invoice Summary
            writer.println("Invoice Summary:");
            writer.println("---------------");
            writer.println("Total Invoices: " + invoices.size());
            writer.println();
            
            if (invoices.isEmpty()) {
                writer.println("No purchases found.");
                writer.println("========================================");
                return;
            }
            
            // Table Header
            writer.println(String.format("%-10s %-40s %-15s %-8s %-12s %-15s",
                "Invoice ID", "Event Name", "Event Type", "Tickets", "Ticket Type", "Total Price"));
            writer.println("--------------------------------------------------------------------------------------------------------");
            
            // List all invoices
            double grandTotal = 0.0;
            for (Invoice invoice : invoices) {
                Event event = invoice.getEvent();
                String eventType = event != null ? getEventType(event) : "N/A";
                String eventName = invoice.getEventName();
                if (eventName.length() > 40) {
                    eventName = eventName.substring(0, 37) + "...";
                }
                
                writer.println(String.format("%-10s %-40s %-15s %-8d %-12s $%-14.2f",
                    invoice.getId(),
                    eventName,
                    eventType,
                    invoice.getQuantity(),
                    invoice.getTicketType(),
                    invoice.getTotalPrice()));
                
                grandTotal += invoice.getTotalPrice();
            }
            
            writer.println("--------------------------------------------------------------------------------------------------------");
            writer.println(String.format("%-10s %-40s %-15s %-8s %-12s $%-14.2f",
                "", "", "", "", "GRAND TOTAL:", grandTotal));
            writer.println();
            
            // Detailed breakdown
            writer.println("Detailed Invoice Information:");
            writer.println("-----------------------------");
            writer.println();
            
            for (int i = 0; i < invoices.size(); i++) {
                Invoice invoice = invoices.get(i);
                Event event = invoice.getEvent();
                
                writer.println("Invoice #" + (i + 1) + " (ID: " + invoice.getId() + ")");
                writer.println("  Event: " + invoice.getEventName());
                if (event != null) {
                    writer.println("  Event Type: " + getEventType(event));
                    writer.println("  Event Date: " + event.getDate());
                    writer.println("  Event Time: " + event.getTime());
                    writer.println("  Venue: " + invoice.getVenueName());
                }
                writer.println("  Ticket Type: " + invoice.getTicketType());
                writer.println("  Quantity: " + invoice.getQuantity());
                writer.println("  Unit Price: $" + String.format("%.2f", invoice.getUnitPrice()));
                writer.println("  Subtotal: $" + String.format("%.2f", invoice.getSubtotal()));
                
                if (invoice.hadMemberDiscount()) {
                    writer.println("  Member Discount (10%): -$" + String.format("%.2f", invoice.getMemberDiscount()));
                    writer.println("  Discounted Subtotal: $" + String.format("%.2f", invoice.getDiscountedSubtotal()));
                }
                
                writer.println("  Sales Tax (8.25%): $" + String.format("%.2f", invoice.getSalesTax()));
                writer.println("  Total Price: $" + String.format("%.2f", invoice.getTotalPrice()));
                writer.println("  Purchase Date: " + invoice.getPurchaseDate());
                writer.println();
            }
            
            // Footer
            writer.println("========================================");
            writer.println("Thank you for using TicketMiner!");
            writer.println("========================================");
        }
    }
}



