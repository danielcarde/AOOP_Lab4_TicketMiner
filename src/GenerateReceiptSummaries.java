import java.io.IOException;

/**
 * Utility class to generate 6 Customer Receipt Summaries.
 * PA3 Requirement 12.
 * 
 * Generates receipts for:
 * - 2 team members (Miranda Macias & Brian Casio)
 * - 4 Disney characters (Mickey Mouse, Donald Duck, Minnie Mouse, Goofy)
 */
public class GenerateReceiptSummaries {
    
    /**
     * Generates 6 Customer Receipt Summaries as required by PA3.
     * 
     * @param dataStore The DataStore containing customers
     * @throws IOException if files cannot be written
     */
    public static void generateRequiredReceipts(DataStore dataStore) throws IOException {
        System.out.println("\n=== Generating 6 Required Receipt Summaries ===");
        
        // Team members
        String[] teamUsernames = {
            "maciasmiranda",      // Miranda Macias
            "casio irachetabrian"  // Brian Casio Iracheta
        };
        
        // Disney characters
        String[] disneyUsernames = {
            "mickeymouse",   // Mickey Mouse
            "donaldduck",    // Donald Duck
            "minniemouse",   // Minnie Mouse
            "goofydisney"    // Goofy
        };
        
        int count = 0;
        
        // Generate receipts for team members
        System.out.println("\nGenerating receipts for team members...");
        for (String username : teamUsernames) {
            Customer customer = dataStore.findCustomer(username);
            if (customer != null) {
                String filename = username.replace(" ", "_") + "_ReceiptSummary.txt";
                try {
                    ReceiptGenerator.generateCustomerReceiptSummary(customer, filename);
                    System.out.println("✓ Generated: " + filename + " for " + customer.getFullName());
                    count++;
                } catch (IOException e) {
                    System.err.println("✗ Error generating receipt for " + username + ": " + e.getMessage());
                }
            } else {
                System.err.println("✗ Customer not found: " + username);
            }
        }
        
        // Generate receipts for Disney characters
        System.out.println("\nGenerating receipts for Disney characters...");
        for (String username : disneyUsernames) {
            Customer customer = dataStore.findCustomer(username);
            if (customer != null) {
                String filename = username + "_ReceiptSummary.txt";
                try {
                    ReceiptGenerator.generateCustomerReceiptSummary(customer, filename);
                    System.out.println("✓ Generated: " + filename + " for " + customer.getFullName());
                    count++;
                } catch (IOException e) {
                    System.err.println("✗ Error generating receipt for " + username + ": " + e.getMessage());
                }
            } else {
                System.err.println("✗ Customer not found: " + username);
            }
        }
        
        System.out.println("\n=== Summary ===");
        System.out.println("Total receipts generated: " + count + " out of 6");
        if (count == 6) {
            System.out.println("✓ All required receipts generated successfully!");
        } else {
            System.out.println("⚠ Some receipts could not be generated.");
        }
    }
}



