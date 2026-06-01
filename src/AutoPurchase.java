import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Handles automatic ticket purchasing from CSV file.
 * PA3 Requirement 8.
 * 
 * Authored by Miranda Macias & Brian Casio for PA3
 */
public class AutoPurchase {
    
    /**
     * Processes automatic purchases from CSV file.
     */
    public static void processAutoPurchases(String filename, DataStore dataStore) {
        System.out.println("\n=== Processing Auto Purchases ===");
        int successCount = 0;
        int failCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();  // Read header to determine format
            if (headerLine == null) {
                System.err.println("Empty file or no header found.");
                return;
            }
            
            // Remove BOM (Byte Order Mark) if present
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            
            // Determine CSV format based on header
            // Use simple split for header since it doesn't have commas in field names
            String[] header = headerLine.split(",");
            for (int i = 0; i < header.length; i++) {
                header[i] = header[i].trim();
            }
            
            boolean hasFirstLastFormat = false;
            int firstIdx = -1, lastIdx = -1, eventIdIdx = -1, quantityIdx = -1, ticketTypeIdx = -1;
            
            for (int i = 0; i < header.length; i++) {
                String col = header[i].trim();
                if (col.equalsIgnoreCase("First")) firstIdx = i;
                else if (col.equalsIgnoreCase("Last")) lastIdx = i;
                else if (col.equalsIgnoreCase("Event ID") || col.equalsIgnoreCase("EventID")) eventIdIdx = i;
                else if (col.equalsIgnoreCase("Ticket Quantity") || col.equalsIgnoreCase("Quantity")) quantityIdx = i;
                else if (col.equalsIgnoreCase("Ticket Type") || col.equalsIgnoreCase("Type")) ticketTypeIdx = i;
            }
            
            // Check if we have the First,Last format
            hasFirstLastFormat = (firstIdx >= 0 && lastIdx >= 0);
            
            // Validate that we found required columns
            if (hasFirstLastFormat && (eventIdIdx < 0 || quantityIdx < 0 || ticketTypeIdx < 0)) {
                System.err.println("Warning: Could not find all required columns in header. Found: " + 
                    "First=" + firstIdx + ", Last=" + lastIdx + ", EventID=" + eventIdIdx + 
                    ", Quantity=" + quantityIdx + ", Type=" + ticketTypeIdx);
                System.err.println("Header columns: " + String.join(", ", header));
                // Don't continue if we can't find required columns
                System.err.println("Error: Cannot process autopurchase file - missing required columns.");
                return;
            }
            
            // Debug: Print detected column indices
            if (hasFirstLastFormat) {
                System.out.println("Detected CSV format - First=" + firstIdx + ", Last=" + lastIdx + 
                    ", EventID=" + eventIdIdx + ", Quantity=" + quantityIdx + ", Type=" + ticketTypeIdx);
            }
            
            // Fallback: assume old format (username, eventId, ticketType, quantity)
            if (!hasFirstLastFormat) {
                eventIdIdx = 1;
                ticketTypeIdx = 2;
                quantityIdx = 3;
            }
            
            // Final validation: ensure we have valid indices
            if (eventIdIdx < 0 || quantityIdx < 0 || ticketTypeIdx < 0) {
                System.err.println("Error: Could not determine column indices. eventIdIdx=" + eventIdIdx + 
                    ", quantityIdx=" + quantityIdx + ", ticketTypeIdx=" + ticketTypeIdx);
                System.err.println("Header columns: " + String.join(", ", header));
                return;
            }
            
            String line;
            int lineNumber = 1;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                // Parse CSV line - use simple split since Event Names may have commas but aren't quoted
                // For this CSV format, we can use simple split as long as Event Name doesn't break the structure
                String[] fields = line.split(",");
                // Trim all fields
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                
                if (fields.length < Math.max(eventIdIdx, Math.max(quantityIdx, ticketTypeIdx)) + 1) {
                    if (failCount < 5) {
                        System.out.println("✗ Line " + lineNumber + ": Insufficient fields (" + fields.length + "), skipping");
                    }
                    failCount++;
                    continue;
                }
                
                try {
                    String username = "";
                    String eventId;
                    String ticketTypeStr;
                    int quantity;
                    Customer customer = null;
                    
                    if (hasFirstLastFormat) {
                        // Format: First,Last,Action,Event ID,Event Name,Ticket Quantity,Ticket Type
                        String firstName = fields[firstIdx].trim();
                        String lastName = fields[lastIdx].trim();
                        
                        // Try multiple username formats to find customer
                        // Format in CustomerList: lastnamefirstname (all lowercase, no spaces)
                        String cleanFirstName = firstName.replaceAll("\\s+", "").toLowerCase();
                        String cleanLastName = lastName.replaceAll("\\s+", "").toLowerCase();
                        
                        // Most common format: lastnamefirstname (no spaces, lowercase)
                        String primaryAttempt = cleanLastName + cleanFirstName;
                        customer = dataStore.findCustomer(primaryAttempt);
                        if (customer != null) {
                            username = primaryAttempt;
                        } else {
                            // Try other variations
                            String[] usernameAttempts = {
                                cleanLastName + firstName.toLowerCase().replaceAll("\\s+", ""), // lastnameFirstname
                                (lastName.replaceAll("\\s+", "") + firstName.replaceAll("\\s+", "")).toLowerCase(), // lastnamefirstname (alternative)
                                (firstName.replaceAll("\\s+", "") + lastName.replaceAll("\\s+", "")).toLowerCase() // firstnamelastname
                            };
                            
                            for (String attempt : usernameAttempts) {
                                customer = dataStore.findCustomer(attempt);
                                if (customer != null) {
                                    username = attempt;
                                    break;
                                }
                            }
                        }
                        
                        // If still not found, try searching by full name (most reliable fallback)
                        if (customer == null) {
                            String fullName = firstName + " " + lastName;
                            // Search through all customers to find by name
                            for (Customer c : dataStore.getAllCustomers()) {
                                String customerFullName = c.getFullName();
                                // Try exact match and also check if names match ignoring case
                                if (customerFullName.equalsIgnoreCase(fullName) || 
                                    customerFullName.equalsIgnoreCase(lastName + ", " + firstName) ||
                                    (customerFullName.contains(firstName) && customerFullName.contains(lastName))) {
                                    customer = c;
                                    username = c.getUsername();
                                    break;
                                }
                            }
                        }
                        
                        // If customer still not found, create a new customer automatically
                        if (customer == null) {
                            // Create new customer with default values
                            username = cleanLastName + cleanFirstName;
                            // Generate a unique username if it already exists
                            int counter = 1;
                            String originalUsername = username;
                            while (dataStore.findCustomer(username) != null) {
                                username = originalUsername + counter;
                                counter++;
                            }
                            
                            // Create customer with default balance (will be checked later)
                            // Default balance: $5000 to allow purchases
                            customer = new Customer(0, firstName, lastName, 5000.0, false, username, "password");
                            dataStore.addCustomer(customer);
                            
                            if (successCount + failCount < 10) {
                                System.out.println("ℹ Created new customer: " + firstName + " " + lastName + 
                                                 " (username: " + username + ", balance: $5000.00)");
                            }
                        }
                        
                        // Debug: verify field extraction
                        if (lineNumber <= 3) {
                            System.out.println("DEBUG Line " + lineNumber + ": fields.length=" + fields.length + 
                                ", eventIdIdx=" + eventIdIdx + ", eventId='" + 
                                (eventIdIdx < fields.length ? fields[eventIdIdx] : "OUT_OF_BOUNDS") + "'");
                        }
                        
                        if (eventIdIdx >= fields.length || quantityIdx >= fields.length || ticketTypeIdx >= fields.length) {
                            if (failCount < 5) {
                                System.out.println("✗ Line " + lineNumber + ": Invalid field indices. Fields: " + fields.length);
                            }
                            failCount++;
                            continue;
                        }
                        
                        eventId = fields[eventIdIdx].trim();
                        quantity = Integer.parseInt(fields[quantityIdx].trim());
                        ticketTypeStr = fields[ticketTypeIdx].trim();
                    } else {
                        // Old format: username, eventId, ticketType, quantity
                        username = fields[0].trim();
                        eventId = fields[eventIdIdx].trim();
                        ticketTypeStr = fields[ticketTypeIdx].trim();
                        quantity = Integer.parseInt(fields[quantityIdx].trim());
                        
                        // Find customer
                        customer = dataStore.findCustomer(username);
                        if (customer == null) {
                            // Create new customer automatically if not found
                            // Default balance: $5000 to allow purchases
                            customer = new Customer(0, "Unknown", "User", 5000.0, false, username, "password");
                            dataStore.addCustomer(customer);
                            
                            if (successCount + failCount < 10) {
                                System.out.println("ℹ Created new customer: " + username + " (balance: $5000.00)");
                            }
                        }
                    }
                    
                    // Find event - trim and normalize the event ID
                    eventId = eventId.trim();
                    Event event = dataStore.findEvent(eventId);
                    
                    // If not found, try without leading zeros or as integer
                    if (event == null && eventId.matches("\\d+")) {
                        // Try as integer (remove leading zeros)
                        String normalizedId = String.valueOf(Integer.parseInt(eventId));
                        event = dataStore.findEvent(normalizedId);
                        if (event != null) {
                            eventId = normalizedId; // Update to normalized ID
                        }
                    }
                    
                    if (event == null) {
                        if (failCount < 5) {
                            System.out.println("✗ Event not found: '" + eventId + "' (line " + lineNumber + ")");
                        }
                        failCount++;
                        continue;
                    }
                    
                    // Parse ticket type
                    TicketType ticketType;
                    String normalizedType = ticketTypeStr.toUpperCase().trim();
                    
                    // Handle common variations
                    if (normalizedType.equals("GA") || normalizedType.equals("GENERAL") || 
                        normalizedType.equals("GENERAL ADMISSION") || normalizedType.equals("GENERALADMISSION")) {
                        ticketType = TicketType.GENERAL_ADMISSION;
                    } else {
                        try {
                            // Try direct enum value
                            normalizedType = normalizedType.replace(" ", "_");
                            ticketType = TicketType.valueOf(normalizedType);
                        } catch (IllegalArgumentException e) {
                            System.out.println("✗ Invalid ticket type: " + ticketTypeStr + " (line " + lineNumber + ")");
                            failCount++;
                            continue;
                        }
                    }
                    
                    // Create invoice (this applies discount and tax automatically)
                    Invoice invoice = new Invoice(customer, event, ticketType, quantity);
                    
                    // Check if customer can afford
                    if (invoice.getTotalPrice() > customer.getBalance()) {
                        if (failCount < 5) {
                            System.out.println("✗ Insufficient funds: " + username + 
                                             " (needs $" + String.format("%.2f", invoice.getTotalPrice()) +
                                             ", has $" + String.format("%.2f", customer.getBalance()) + ")");
                        }
                        failCount++;
                        continue;
                    }
                    
                    // Check if tickets available
                    if (!event.sellTickets(ticketType, quantity)) {
                        if (failCount < 5) {
                            System.out.println("✗ Not enough tickets: " + eventId + " " + ticketTypeStr);
                        }
                        failCount++;
                        continue;
                    }
                    
                    // Process purchase
                    customer.charge(invoice.getTotalPrice());
                    customer.addInvoice(invoice);
                    event.addTicketPurchase(invoice);
                    
                    // Only print details for first 10 purchases, then show progress
                    if (successCount < 10) {
                        System.out.println("✓ " + username + " purchased " + quantity + " " + 
                                         ticketTypeStr + " ticket(s) for " + event.getName() +
                                         " ($" + String.format("%.2f", invoice.getTotalPrice()) + ")");
                    } else if (successCount == 10) {
                        System.out.println("... (showing summary only for remaining purchases)");
                    } else if (successCount % 1000 == 0) {
                        System.out.print("."); // Progress indicator
                    }
                    successCount++;
                    
                    Logger.log("AutoPurchase: " + username + " - Event " + eventId + 
                             " - " + ticketType + " x" + quantity);
                    
                } catch (Exception e) {
                    if (failCount < 5) {
                        System.out.println("✗ Error processing line " + lineNumber + ": " + e.getMessage());
                    }
                    failCount++;
                }
            }
            
            System.out.println("\n=== Auto Purchase Summary ===");
            System.out.println("Total lines processed: " + (lineNumber - 1));
            System.out.println("Successful: " + successCount);
            System.out.println("Failed: " + failCount);
            if (successCount > 0) {
                double successRate = (successCount * 100.0) / (successCount + failCount);
                System.out.printf("Success rate: %.2f%%%n", successRate);
            }
            System.out.println("=============================\n");
            
        } catch (IOException e) {
            System.err.println("Error reading autopurchase file: " + e.getMessage());
        }
    }
    
    /**
     * Parses a CSV line, handling commas within fields.
     * Simple parser that splits on commas but respects quoted fields.
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        // Add the last field
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }
}