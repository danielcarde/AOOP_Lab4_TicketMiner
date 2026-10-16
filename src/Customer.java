import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the ticket system.
 * 
 * Core structure taken from Miranda Macias:
 * - Simple fields: username, password, fullName, balance
 * - List<Invoice> for invoice tracking
 * - Methods: checkPassword(), charge(), addInvoice()
 * 
 * Additional functionality taken from Brian Casio:
 * - id field for customer identification
 * - firstName and lastName fields (separate from fullName)
 * - ticketMinerMembership field for discount functionality
 * - authenticate() method (alias for checkPassword with username)
 * - canAfford() method
 * - purchaseTicket() method that creates invoices automatically
 * - displayCustomerInfo() method
 * - getMoneyAvailable() method (alias for getBalance())
 * - getUserName() and getPassword() getters
 */
public class Customer {
    // Core fields from Miranda Macias
    private String username;
    private String password;
    private String fullName;
    private double balance;
    private double totalSavings;
    // Historical concert count for the alternative teamgd CSV adapter.
    private int legacyConcertsPurchased;
    private List<Invoice> invoices = new ArrayList<>();
    
    // Additional fields from Brian Casio
    private int id;
    private String firstName;
    private String lastName;
    private boolean ticketMinerMembership;

    /**
     * Constructor from Miranda Macias.
     * 
     * @param username Customer username
     * @param password Customer password
     * @param fullName Customer full name
     * @param balance Starting balance
     */
    public Customer(String username, String password, String fullName, double balance) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.balance = balance;
        this.totalSavings = 0.0;
        this.ticketMinerMembership = false;
        
        // Parse fullName into firstName and lastName
        String[] names = fullName.split(" ", 2);
        this.firstName = names.length > 0 ? names[0] : "";
        this.lastName = names.length > 1 ? names[1] : "";
    }

    /**
     * Constructor from Brian Casio (enhanced version).
     * 
     * @param id Customer ID
     * @param firstName Customer first name
     * @param lastName Customer last name
     * @param moneyAvailable Starting balance
     * @param ticketMinerMembership Whether customer has membership
     * @param userName Customer username
     * @param password Customer password
     */
    public Customer(int id, String firstName, String lastName, double moneyAvailable,
                    boolean ticketMinerMembership, String userName, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.balance = moneyAvailable;
        this.ticketMinerMembership = ticketMinerMembership;
        this.username = userName;
        this.password = password;
        this.totalSavings = 0.0;
    }

    /** Compatibility with the original teamgd CSV constructor order. */
    public Customer(int id, String firstName, String lastName, String username,
                    String password, double balance, int concertsPurchased, boolean member) {
        this(id, firstName, lastName, balance, member, username, password);
        this.legacyConcertsPurchased = concertsPurchased;
    }
    public double getSavedMoney() { return getTotalSavings(); }
    public int getConcertsPurchased() { return legacyConcertsPurchased; }
    public boolean isTicketMinerMembership() { return hasTicketMinerMembership(); }

    // ========== Getters from Miranda Macias ==========
    
    public String getUsername() {
        return username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public List<Invoice> getInvoices() {
        return List.copyOf(invoices);
    }

    // ========== Getters from Brian Casio ==========
    
    public int getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public double getMoneyAvailable() {
        return balance;
    }
    
    public boolean hasTicketMinerMembership() {
        return ticketMinerMembership;
    }
    
    public String getUserName() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    public double getTotalSavings(){
        return totalSavings;
    }

    // ========== Authentication Methods ==========
    
    /**
     * Checks if the provided password matches.
     * Taken from Miranda Macias.
     */
    public boolean checkPassword(String p) {
        return password.equals(p);
    }
    
    /**
     * Authenticates user with username and password.
     * Taken from Brian Casio.
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // ========== Financial Methods ==========
    
    /**
     * Charges the customer's account.
     * Taken from Miranda Macias.
     */
    public boolean charge(double amt) {
        if (amt > balance) return false;
        balance -= amt;
        return true;
    }
    
    /**
     * Checks if customer can afford a purchase.
     * Taken from Brian Casio.
     */
    public boolean canAfford(double amount) {
        return balance >= amount;
    }
    
    /**
     * Processes a ticket purchase.
     * Taken from Brian Casio.
     * This method handles the entire purchase process including invoice creation.
     * 
     * @param amount Total cost of the purchase
     * @param event Event being purchased for
     * @param ticketType Type of ticket (as string)
     * @param quantity Number of tickets
     * @return true if purchase was successful, false otherwise
     */
    public boolean purchaseTicket(double amount, Event event, String ticketType, int quantity) {
        if (!canAfford(amount)) {
            return false;
        }
        
        balance -= amount;
        
        // Create invoice - need to convert string ticketType to TicketType enum
        TicketType type;
        try {
            type = TicketType.valueOf(ticketType.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Handle "General" vs "GENERAL_ADMISSION"
            if (ticketType.equalsIgnoreCase("general")) {
                type = TicketType.GENERAL_ADMISSION;
            } else {
                return false; // Invalid ticket type
            }
        }
        
        Invoice invoice = new Invoice(this, event, type, quantity);
        addInvoice(invoice);
        
        // Add ticket purchase to event if method exists
        if (event != null) {
            event.addTicketPurchase(invoice);
        }
        
        return true;
    }

    // ========== Invoice Management ==========
    
    /**
     * Adds an invoice to the customer's purchase history.
     * Taken from Miranda Macias.
     */
    public void addInvoice(Invoice inv) {
        invoices.add(inv);

        // Tracks total savings from being a member
        if(inv.hadMemberDiscount()){
            totalSavings += inv.getMemberDiscount();
        }
    }
    
    /**
     * Removes an invoice from the customer's purchase history.
     * PA4 Requirement 3 - For ticket cancellation.
     */
    public boolean removeInvoice(Invoice inv) {
        boolean removed = invoices.remove(inv);
        if (removed && inv.hadMemberDiscount()) {
            // Adjust total savings if invoice had a discount
            totalSavings -= inv.getMemberDiscount();
            if (totalSavings < 0) {
                totalSavings = 0; // Prevent negative savings
            }
        }
        return removed;
    }

    // ========== Display Methods ==========
    
    /**
     * Displays customer information.
     * Taken from Brian Casio.
     */
    public void displayCustomerInfo() {
        System.out.println();
        System.out.println("=== Customer Information ===");
        System.out.println("Name: " + getFullName());
        System.out.println("Money Available: $" + String.format("%.2f", balance));
        System.out.println("TicketMiner Member: " + (ticketMinerMembership ? "Yes" : "No"));

        // Show total savings for members
        if(ticketMinerMembership && totalSavings > 0){
            System.out.println("Total Savings $" + String.format("%.2f", totalSavings));
        }
        System.out.println("Total Purchases: " + invoices.size() + " transactions");
        
        if (invoices.size() > 0) {
            System.out.println();
            System.out.println("=== Recent Purchases ===");
            int start = Math.max(0, invoices.size() - 3);
            for (int i = start; i < invoices.size(); i++) {
                Invoice inv = invoices.get(i);
                System.out.println("- " + inv.getTicketType() + " ticket for " + 
                                 inv.getEventName() + " ($" + 
                                 String.format("%.2f", inv.getTotalPrice()) + ")");
            }
        }
    }

    /**
     * String representation of the customer.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        return String.format("%s (%s), $%.2f, Invoices: %d",
                             fullName, username, balance, invoices.size());
    }
}

