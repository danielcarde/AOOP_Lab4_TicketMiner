import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a purchase invoice for ticket transactions.
 * 
 * Core structure taken from Miranda Macias:
 * - Uses TicketType enum
 * - Auto-generates ID with static counter
 * - LocalDateTime timestamp
 * - Direct references to Customer and Event objects
 * - Simple toString() method
 * 
 * Additional functionality taken from Brian Casio:
 * - Individual ID fields (invoiceId, customerId, eventId)
 * - String fields for eventName, customerName, venueName
 * - purchaseDate string field
 * - getTax() and getTotalWithTax() methods
 * - isVIPTicket() and isMemberEligible() helper methods
 * - displayInvoice() method for formatted output
 * - Additional getters for all fields
 * 
 * PA4 Enhancements:
 * - Fee system (convenience, service, charity fees)
 * - Fees calculated before discount
 * - Discount applied to subtotal (ticket costs + fees)
 */
public class Invoice {
    // Decimal arithmetic preserves the existing round-down policy at exact cents.
    private static double percentageRoundedDown(double amount, String rate) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(rate))
            .setScale(2, RoundingMode.FLOOR).doubleValue();
    }

    // Core fields from Miranda Macias
    private static int nextId = 1;
    private final String id;
    private final Customer customer;
    private final Event event;
    private final TicketType type;
    private final int quantity;
    private final double unitPrice;
    private double totalPrice;
    
    // PA4 Requirement 2: Fee fields
    private final double ticketCosts;         // Cost of tickets only (unitPrice * qty)
    private final double convenienceFee;      // $2.50 flat fee per transaction
    private final double serviceFee;          // 0.5% of ticket costs
    private final double charityFee;          // 0.75% of ticket costs
    private final double totalFees;           // Sum of all fees
    
    private final double subtotal;            // Ticket costs + fees (before discount)
    private final double memberDiscount;
    private final double discountedSubtotal;
    private final double salesTax;
    private LocalDateTime timeStamp;  // Not final to allow assignment in second constructor
    
    // Additional fields from Brian Casio (for compatibility and reporting)
    private int invoiceId;  // Numeric ID for compatibility
    private int customerId;
    private String eventIdString;  // String version for compatibility
    private String ticketTypeString;  // String version for compatibility
    private String purchaseDate;
    private String eventName;
    private String customerName;
    private String venueName;
    
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor from Miranda Macias.
     * This is the primary constructor that should be used.
     * 
     * @param c Customer making the purchase
     * @param e Event being purchased for
     * @param t Ticket type
     * @param qty Quantity of tickets
     */
    public Invoice(Customer c, Event e, TicketType t, int qty) {
        this.customer = c;
        this.event = e;
        this.type = t;
        this.quantity = qty;
        this.unitPrice = e.getPrice(t);
        this.id = String.valueOf(nextId++);  // Auto-generate and increment
        this.timeStamp = LocalDateTime.now();

        // PA4 Step 1: Calculate base ticket costs
        this.ticketCosts = unitPrice * qty;
        
        // PA4 Step 2: Calculate fees (BEFORE any discount)
        this.convenienceFee = 2.50;  // Flat $2.50 per transaction
        this.serviceFee = percentageRoundedDown(ticketCosts, "0.005");  // 0.5% of tickets
        this.charityFee = percentageRoundedDown(ticketCosts, "0.0075"); // 0.75% of tickets
        this.totalFees = convenienceFee + serviceFee + charityFee;
        
        // PA4 Step 3: Calculate subtotal (ticket costs + fees)
        this.subtotal = ticketCosts + totalFees;

        // PA4 Step 4: Apply discount to subtotal (Discount Strategy)
        DiscountStrategy discountStrategy;
        if (c.hasTicketMinerMembership()) {
            discountStrategy = new MemberDiscountStrategy();
        } else {
            discountStrategy = new NoDiscountStrategy();
        }
        
        this.memberDiscount = discountStrategy.calculateDiscount(subtotal, t);
        this.discountedSubtotal = subtotal - memberDiscount;
        
        // PA4 Step 5: Apply Texas sales tax (8.25% on discounted subtotal)
        this.salesTax = percentageRoundedDown(discountedSubtotal, "0.0825");
        
        // PA4 Step 6: Calculate final total
        this.totalPrice = discountedSubtotal + salesTax;

        // Populate additional fields from Brian Casio for compatibility
        this.invoiceId = Integer.parseInt(this.id);
        this.customerId = c.getId();
        this.eventIdString = e.getId();
        this.ticketTypeString = t.name();
        this.purchaseDate = timeStamp.format(FMT);
        this.eventName = e.getName();
        this.customerName = c.getFullName();
        this.venueName = e.getVenue().getName();
    }

    /**
     * Constructor from Brian Casio (for backward compatibility).
     * Creates invoice with all explicit fields.
     * 
     * @param invoiceId Invoice ID
     * @param customerId Customer ID
     * @param eventId Event ID
     * @param ticketType Ticket type as string
     * @param quantity Quantity
     * @param unitPrice Unit price
     * @param totalPrice Total price
     * @param purchaseDate Purchase date string
     * @param eventName Event name
     * @param customerName Customer name
     * @param venueName Venue name
     */
    public Invoice(int invoiceId, int customerId, int eventId, String ticketType, 
                   int quantity, double unitPrice, double totalPrice, 
                   String purchaseDate, String eventName, String customerName, 
                   String venueName) {
        // Initialize final fields first
        this.quantity = quantity;
        this.unitPrice = unitPrice;

        // PA4: Calculate fees for legacy invoices
        this.ticketCosts = unitPrice * quantity;
        this.convenienceFee = 2.50;
        this.serviceFee = percentageRoundedDown(ticketCosts, "0.005");
        this.charityFee = percentageRoundedDown(ticketCosts, "0.0075");
        this.totalFees = convenienceFee + serviceFee + charityFee;
        
        this.subtotal = ticketCosts + totalFees;
        this.memberDiscount = 0.0;  // Unknown for legacy invoices
        this.discountedSubtotal = this.subtotal;  // Assume no discount for legacy
        this.salesTax = totalPrice - this.subtotal;  // Calculate tax as difference
        this.totalPrice = totalPrice;
        
        // Parse purchase date for timestamp
        LocalDateTime parsedTime;
        try {
            parsedTime = LocalDateTime.parse(purchaseDate, FMT);
        } catch (Exception e) {
            parsedTime = LocalDateTime.now();
        }
        this.timeStamp = parsedTime;
        
        // Convert string ticket type to enum
        TicketType tempType;
        try {
            tempType = TicketType.valueOf(ticketType.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Handle "General" vs "GENERAL_ADMISSION"
            if (ticketType.equalsIgnoreCase("general")) {
                tempType = TicketType.GENERAL_ADMISSION;
            } else {
                tempType = TicketType.VIP; // Default fallback
            }
        }
        this.type = tempType;
        
        // Initialize other fields
        this.invoiceId = invoiceId;
        this.id = String.valueOf(invoiceId);
        this.customerId = customerId;
        this.eventIdString = String.valueOf(eventId);
        this.ticketTypeString = ticketType;
        this.purchaseDate = purchaseDate;
        this.eventName = eventName;
        this.customerName = customerName;
        this.venueName = venueName;
        
        // Set nulls for customer and event (not available in this constructor)
        this.customer = null;
        this.event = null;
    }

    // ========== Getters from Miranda Macias ==========
    
    public String getId() {
        return id;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public TicketType getType() {
        return type;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
    /**
     * Gets the subtotal before discount.
     */
    public double getSubtotal(){
        return subtotal;
    }
    /**
     * Gets the member discount amount.
     */
    public double getMemberDiscount(){
        return memberDiscount;
    }
    /**
     * Gets the subtotal after discount, before tax.
     */
    public double getDiscountedSubtotal(){
        return discountedSubtotal;
    }
    /**
     * Gets the sales tax amount (8.25%).
     */
    public double getSalesTax(){
        return salesTax;
    }
    public boolean hadMemberDiscount(){
        return memberDiscount > 0;
    } 

    // ========== PA4 Fee Getters (Requirement 2) ==========
    
    /**
     * Gets the base ticket costs (before fees).
     * PA4 Requirement 2.
     */
    public double getTicketCosts() {
        return ticketCosts;
    }

    /**
     * Gets the convenience fee ($2.50 flat).
     * PA4 Requirement 2.
     */
    public double getConvenienceFee() {
        return convenienceFee;
    }

    /**
     * Gets the service fee (0.5% of ticket costs).
     * PA4 Requirement 2.
     */
    public double getServiceFee() {
        return serviceFee;
    }

    /**
     * Gets the charity fee (0.75% of ticket costs).
     * PA4 Requirement 2.
     */
    public double getCharityFee() {
        return charityFee;
    }

    /**
     * Gets total fees (convenience + service + charity).
     * PA4 Requirement 2.
     */
    public double getTotalFees() {
        return totalFees;
    }

    // ========== Getters from Brian Casio ==========
    
    public int getInvoiceId() {
        return invoiceId != 0 ? invoiceId : Integer.parseInt(id);
    }
    
    public int getCustomerId() {
        return customer != null ? customer.getId() : customerId;
    }
    
    public int getEventId() {
        if (event != null) {
            try {
                return Integer.parseInt(event.getId());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        try {
            return Integer.parseInt(eventIdString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String getTicketType() {
        return ticketTypeString != null ? ticketTypeString : type.name();
    }
    
    public String getPurchaseDate() {
        return purchaseDate != null ? purchaseDate : timeStamp.format(FMT);
    }
    
    public String getEventName() {
        return eventName != null ? eventName : (event != null ? event.getName() : "");
    }
    
    public String getCustomerName() {
        return customerName != null ? customerName : (customer != null ? customer.getFullName() : "");
    }
    
    public String getVenueName() {
        return venueName != null ? venueName : (event != null ? event.getVenue().getName() : "");
    }

    // ========== Helper Methods from Brian Casio ==========
    
    /**
     * Taken from Brian Casio.
     */
    public double getTax() {
        return salesTax;
    }
    
    /**
     * Gets total price including tax.
     * Taken from Brian Casio.
     */
    //public double getTotalWithTax() {
        //return totalPrice + getTax();
    //}
    
    /**
     * Checks if this is a VIP ticket.
     * Taken from Brian Casio.
     */
    public boolean isVIPTicket() {
        return type == TicketType.VIP;
    }
    
    /**
     * Checks if ticket is eligible for member discount.
     * Taken from Brian Casio.
     */
    public boolean isMemberEligible() {
        return !isVIPTicket(); // Members get discounts on non-VIP tickets
    }

    // ========== Display Methods ==========
    
    /**
     * String representation of the invoice.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        String formattedTime = timeStamp.format(FMT);
        return String.format(
            "Invoice ID: %s%nCustomer: %s%nEvent: %s%nType: %s %d%nUnit: $%.2f%nTotal: $%.2f%nDate: %s",
            id, getCustomerName(), getEventName(), type, quantity,
            unitPrice, totalPrice, formattedTime
        );
    }
    
    /**
     * Displays the invoice with formatted output.
     * Taken from Brian Casio.
     */
    public void displayInvoice(Event event) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("        INVOICE #" + getInvoiceId());
        System.out.println("========================================");
        System.out.println("Customer: " + getCustomerName());

        //Shows member status if applicable
        if(customer != null && customer.hasTicketMinerMembership()){
            System.out.println("          [TicketMiner Member]");
        }

        System.out.println();
        System.out.println("Event: " + getEventName());
        System.out.println("Venue: " + getVenueName());
        System.out.println("Ticket Type: " + getTicketType());
        System.out.println("Quantity: " + quantity);
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("PRICING BREAKDOWN:");
        System.out.println("----------------------------------------");
        System.out.println("Unit Price: $" + String.format("%.2f", unitPrice));
        System.out.println("Ticket Costs (" + quantity + " x $" + 
                         String.format("%.2f", unitPrice) + "): $" + 
                         String.format("%.2f", ticketCosts));
        System.out.println();
        System.out.println("FEES:");
        System.out.println("  Convenience Fee: $" + String.format("%.2f", convenienceFee));
        System.out.println("  Service Fee (0.5%): $" + String.format("%.2f", serviceFee));
        System.out.println("  Charity Fee (0.75%): $" + String.format("%.2f", charityFee));
        System.out.println("  Total Fees: $" + String.format("%.2f", totalFees));
        System.out.println();
        System.out.println("Subtotal (Tickets + Fees): $" + String.format("%.2f", subtotal));

        // Discount application
        if(memberDiscount > 0){
            System.out.println("Member Discount (10%): -$" + String.format("%.2f", memberDiscount));
            System.out.println("Discounted Subtotal: $" + String.format("%.2f", discountedSubtotal));
        }
        
        System.out.println("Sales Tax (8.25%): $" + String.format("%.2f", salesTax)); 
        System.out.println("========================================");
        System.out.println("TOTAL: $" + String.format("%.2f", totalPrice)); 
        System.out.println("========================================");
        System.out.println();
        System.out.println("Purchase Date: " + getPurchaseDate());
        System.out.println("Confirmation: INV-" + id);
        System.out.println("========================================");
        if (event != null) {
            System.out.println();
            System.out.println("--- EVENT INFORMATION ---");
            // Use getTotalSeatsSold() instead of getTicketCount() for compatibility
            System.out.println("Total Tickets Sold for this Event: " + event.getTotalSeatsSold());
            System.out.println("----------------------------");
        }
    }
}

