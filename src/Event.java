import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract base class for events.
 * 
 * Core structure taken from Miranda Macias:
 * - TicketInventory interface implementation
 * - LocalDateTime for date/time handling
 * - Map<TicketType, Double> for prices
 * - EnumMap for tracking sales and revenue
 * - String ID type
 * 
 * Additional functionality taken from Brian Casio:
 * - Invoice[] tracking for purchase history
 * - getDate() and getTime() string getters
 * - getPriceByType(int) method for backward compatibility
 * - displayEventDetails() and displayEventDetailsForCustomer() methods
 * - actualProfit() calculation including venue cost
 * - Individual price fields and getters for each ticket type
 * - getTotalSeatsSold() and get*SeatsSold() methods for reporting
 */
public abstract class Event implements TicketInventory {
    // Core fields from Miranda Macias
    private String id;
    private String name;
    private LocalDateTime dateTime;
    private Venue venue;
    private Map<TicketType, Double> priceMap;
    
    // Inventory & revenue tracking from Miranda Macias
    private final Map<TicketType, Integer> sold = new EnumMap<>(TicketType.class);
    private final Map<TicketType, Double> revenue = new EnumMap<>(TicketType.class);
    private double totalTaxCollected; // Total tax collected for event
    private double totalMemberDiscounts; // Total amount of member discounts given
    
    // PA4 Requirement 2: Fee tracking
    private double totalConvenienceFees;
    private double totalServiceFees;
    private double totalCharityFees;
    
    // Invoice tracking from Brian Casio
    private Invoice[] ticketsPurchased = new Invoice[1000];
    private int ticketCount = 0;
    
    // Fireworks fields - PA3 Requirement 7c
    private boolean hasFireworks = false;
    private double fireworksCost = 0.0;
    
    // Individual price fields from Brian Casio (for backward compatibility)
    private double vipPrice;
    private double goldPrice;
    private double silverPrice;
    private double bronzePrice;
    private double generalPrice;
    private String eventType;

    /**
     * Constructor for Event.
     * Taken from Miranda Macias (base structure)
     * Enhanced with eventType from Brian Casio
     */
    protected Event(String id, String name, LocalDateTime dateTime, Venue venue, 
                    Map<TicketType, Double> prices, String eventType) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.venue = venue;
        this.priceMap = prices;
        this.eventType = eventType;
        
        // Extract individual prices from map for backward compatibility (from Brian Casio)
        this.vipPrice = prices.getOrDefault(TicketType.VIP, 0.0);
        this.goldPrice = prices.getOrDefault(TicketType.GOLD, 0.0);
        this.silverPrice = prices.getOrDefault(TicketType.SILVER, 0.0);
        this.bronzePrice = prices.getOrDefault(TicketType.BRONZE, 0.0);
        this.generalPrice = prices.getOrDefault(TicketType.GENERAL_ADMISSION, 0.0);
        this.totalTaxCollected = 0.0;
        this.totalMemberDiscounts = 0.0;
        
        // Initialize PA4 fee tracking (Requirement 2)
        this.totalConvenienceFees = 0.0;
        this.totalServiceFees = 0.0;
        this.totalCharityFees = 0.0;

        // Initialize tracking maps (from Miranda Macias)
        for (TicketType t : TicketType.values()) {
            sold.put(t, 0);
            revenue.put(t, 0.0);
        }
    }

    // ========== Getters from Miranda Macias ==========
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public Venue getVenue() {
        return venue;
    }
    
    public Map<TicketType, Double> getPriceMap() {
        return priceMap;
    }
    
    public double getPrice(TicketType t) {
        return priceMap.getOrDefault(t, 0.0);
    }
    
    public int sold(TicketType type) {
        return sold.getOrDefault(type, 0);
    }

    // ========== Getters from Brian Casio ==========
    
    /**
     * Gets the date as a formatted string.
     * Taken from Brian Casio for backward compatibility.
     */
    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return dateTime.format(formatter);
    }
    
    /**
     * Gets the time as a formatted string.
     * Taken from Brian Casio for backward compatibility.
     */
    public String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return dateTime.format(formatter);
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public double getVipPrice() {
        return vipPrice;
    }
    
    public double getGoldPrice() {
        return goldPrice;
    }
    
    public double getSilverPrice() {
        return silverPrice;
    }
    
    public double getBronzePrice() {
        return bronzePrice;
    }
    
    public double getGeneralPrice() {
        return generalPrice;
    }
    public double getTotalTaxCollected(){
        return totalTaxCollected;
    }
    public double getTotalMemberDiscounts(){
        return totalMemberDiscounts;
    }
    
    // ========== PA4 Fee Getters (Requirement 2) ==========
    
    /**
     * Gets total convenience fees collected for this event.
     * PA4 Requirement 2.
     */
    public double getTotalConvenienceFees() {
        return totalConvenienceFees;
    }

    /**
     * Gets total service fees collected for this event.
     * PA4 Requirement 2.
     */
    public double getTotalServiceFees() {
        return totalServiceFees;
    }

    /**
     * Gets total charity fees collected for this event.
     * PA4 Requirement 2.
     */
    public double getTotalCharityFees() {
        return totalCharityFees;
    }

    /**
     * Gets total of all fees collected for this event.
     * PA4 Requirement 2.
     */
    public double getTotalFeesCollected() {
        return totalConvenienceFees + totalServiceFees + totalCharityFees;
    }
    
    // Fireworks getters - PA3 Requirement 7c
    public boolean hasFireworks() {
        return hasFireworks;
    }
    
    public double getFireworksCost() {
        return fireworksCost;
    }
    
    // Fireworks setters - PA3 Requirement 7c
    public void setFireworks(boolean hasFireworks, double fireworksCost) {
        this.hasFireworks = hasFireworks;
        this.fireworksCost = hasFireworks ? fireworksCost : 0.0;
    }

    // ========== TicketInventory Methods from Miranda Macias ==========
    
    @Override
    public boolean sellTickets(TicketType type, int qty) {
        int totalSold = sold.values().stream().mapToInt(i -> i).sum();
        if (totalSold + qty > venue.getCapacity()) return false;
        sold.put(type, sold.get(type) + qty);
        revenue.put(type, revenue.get(type) + qty * getPrice(type));
        return true;
    }

    @Override
    public int remaining(TicketType type) {
        // Any type can use any remaining seat
        return totalRemaining();
    }

    @Override
    public int totalRemaining() {
        int totalSold = sold.values().stream().mapToInt(i -> i).sum();
        return venue.getCapacity() - totalSold;
    }
    
    @Override
    public double revenue(TicketType type) {
        return revenue.getOrDefault(type, 0.0);
    }

    @Override
    public double totalRevenue() {
        return revenue.values().stream().mapToDouble(d -> d).sum();
    }

    @Override
    public double expectedProfit() {
        // Assume every seat sold at its listed price
        // Updated to include venue cost from Brian Casio
        double sum = 0;
        int cap = venue.getCapacity();
        int perType = cap / TicketType.values().length;
        for (TicketType t : TicketType.values()) {
            sum += perType * getPrice(t);
        }
        return sum - venue.getCost();
    }

    @Override
    public double actualProfit() {
        // PA4 Enhancement: Profit now includes all fees collected
        double totalIncome = totalRevenue() + getTotalFeesCollected();
        return totalIncome - venue.getCost();
    }

    // ========== Invoice Tracking Methods from Brian Casio ==========
    
    /**
     * Adds a ticket purchase invoice to this event.
     * Taken from Brian Casio.
     * Enhanced for PA4 to track fees (Requirement 2).
     */
    public void addTicketPurchase(Invoice invoice) {
        if (ticketCount < ticketsPurchased.length) {
            ticketsPurchased[ticketCount] = invoice;
            ticketCount++;

            //tracking tax and discounts
            totalTaxCollected += invoice.getSalesTax();
            totalMemberDiscounts += invoice.getMemberDiscount();
            
            // PA4 Requirement 2: Track fees
            totalConvenienceFees += invoice.getConvenienceFee();
            totalServiceFees += invoice.getServiceFee();
            totalCharityFees += invoice.getCharityFee();
        }
    }
    
    /**
     * Removes fees from an invoice when a purchase is cancelled.
     * PA4 Requirement 4c - Used when event is cancelled.
     * 
     * @param invoice The invoice to remove fees from
     */
    public void removeTicketPurchaseFees(Invoice invoice) {
        // PA4: Remove fees when refunding
        totalConvenienceFees -= invoice.getConvenienceFee();
        totalServiceFees -= invoice.getServiceFee();
        totalCharityFees -= invoice.getCharityFee();
        
        // Ensure fees don't go negative
        if (totalConvenienceFees < 0) totalConvenienceFees = 0.0;
        if (totalServiceFees < 0) totalServiceFees = 0.0;
        if (totalCharityFees < 0) totalCharityFees = 0.0;
        
        // Also remove tax and discount tracking
        totalTaxCollected -= invoice.getSalesTax();
        totalMemberDiscounts -= invoice.getMemberDiscount();
        
        if (totalTaxCollected < 0) totalTaxCollected = 0.0;
        if (totalMemberDiscounts < 0) totalMemberDiscounts = 0.0;
    }
    
    /**
     * Resets all fees to zero.
     * PA4 Requirement 4c - Used when entire event is cancelled.
     */
    public void resetAllFees() {
        totalConvenienceFees = 0.0;
        totalServiceFees = 0.0;
        totalCharityFees = 0.0;
    }
    
    /**
     * Gets all tickets purchased for this event.
     * Taken from Brian Casio.
     */
    public Invoice[] getTicketsPurchased() {
        Invoice[] result = new Invoice[ticketCount];
        for (int i = 0; i < ticketCount; i++) {
            result[i] = ticketsPurchased[i];
        }
        return result;
    }
    
    /**
     * Gets the count of ticket purchases.
     * Taken from Brian Casio.
     */
    public int getTicketCount() {
        return ticketCount;
    }

    // ========== Seat Calculation Methods from Brian Casio ==========
    
    /**
     * Calculates total seats sold from invoices.
     * Taken from Brian Casio.
     */
    public int getTotalSeatsSold() {
        // Use EnumMap data from Miranda Macias as primary source
        return sold.values().stream().mapToInt(i -> i).sum();
    }
    
    /**
     * Gets VIP seats sold.
     * Taken from Brian Casio (method signature), uses EnumMap data from Miranda Macias.
     */
    public int getVipSeatsSold() {
        return sold(TicketType.VIP);
    }
    
    public int getGoldSeatsSold() {
        return sold(TicketType.GOLD);
    }
    
    public int getSilverSeatsSold() {
        return sold(TicketType.SILVER);
    }
    
    public int getBronzeSeatsSold() {
        return sold(TicketType.BRONZE);
    }
    
    public int getGeneralSeatsSold() {
        return sold(TicketType.GENERAL_ADMISSION);
    }
    
    // ========== Revenue Calculation Methods from Brian Casio ==========
    
    public double getTotalVipRevenue() {
        return revenue(TicketType.VIP);
    }
    
    public double getTotalGoldRevenue() {
        return revenue(TicketType.GOLD);
    }
    
    public double getTotalSilverRevenue() {
        return revenue(TicketType.SILVER);
    }
    
    public double getTotalBronzeRevenue() {
        return revenue(TicketType.BRONZE);
    }
    
    public double getTotalGeneralRevenue() {
        return revenue(TicketType.GENERAL_ADMISSION);
    }

    // ========== Helper Methods from Brian Casio ==========
    
    /**
     * Gets ticket type name by integer (for backward compatibility).
     * Taken from Brian Casio.
     */
    public String getTicketTypeName(int ticketType) {
        switch (ticketType) {
            case 1: return "VIP";
            case 2: return "Gold";
            case 3: return "Silver";
            case 4: return "Bronze";
            case 5: return "General";
            default: return "Unknown";
        }
    }
    
    /**
     * Gets price by ticket type integer (for backward compatibility).
     * Taken from Brian Casio.
     */
    public double getPriceByType(int ticketType) {
        switch (ticketType) {
            case 1: return vipPrice;
            case 2: return goldPrice;
            case 3: return silverPrice;
            case 4: return bronzePrice;
            case 5: return generalPrice;
            default: return 0.0;
        }
    }
    
    /**
     * Gets expected profit at sell-out.
     * Taken from Brian Casio (renamed from getExpectedProfitSellOut).
     */
    public double getExpectedProfitSellOut() {
        return expectedProfit();
    }

    // ========== Display Methods from Brian Casio ==========
    
    /**
     * Displays detailed event information for administrators.
     * Taken from Brian Casio.
     */
    public void displayEventDetails() {
        System.out.println("=====================");
        System.out.println("Event ID: " + id);
        System.out.println(name);
        System.out.println(getDate());
        System.out.println(getTime());
        System.out.println("Event Type: " + getEventType());
        System.out.println("Event Capacity: " + venue.getCapacity());
        System.out.println("Total Seats sold: " + getTotalSeatsSold());
        System.out.println("Total VIP Seats Sold: " + getVipSeatsSold());
        System.out.println("Total Gold Seats Sold: " + getGoldSeatsSold());
        System.out.println("Total Silver Seats Sold: " + getSilverSeatsSold());
        System.out.println("Total Bronze Seats Sold: " + getBronzeSeatsSold());
        System.out.println("Total General Adm Seats Sold: " + getGeneralSeatsSold());
        System.out.printf("Total revenue for VIP tickets: $%.2f%n", getTotalVipRevenue());
        System.out.printf("Total revenue for Gold tickets: $%.2f%n", getTotalGoldRevenue());
        System.out.printf("Total revenue for Silver tickets: $%.2f%n", getTotalSilverRevenue());
        System.out.printf("Total revenue for Bronze tickets: $%.2f%n", getTotalBronzeRevenue());
        System.out.printf("Total revenue for General Admission tickets: $%.2f%n", getTotalGeneralRevenue());
        System.out.printf("Total revenue for all tickets: $%.2f%n", totalRevenue());
        System.out.printf("Total tax collected: $%.2f%n", totalTaxCollected);
        System.out.printf("Total member discounts given: $%.2f%n", totalMemberDiscounts);
        
        // PA4 Requirement 2 - Fee information
        System.out.println();
        System.out.println("--- FEES COLLECTED (PA4) ---");
        System.out.printf("Convenience Fees: $%.2f%n", totalConvenienceFees);
        System.out.printf("Service Fees: $%.2f%n", totalServiceFees);
        System.out.printf("Charity Fees: $%.2f%n", totalCharityFees);
        System.out.printf("Total Fees: $%.2f%n", getTotalFeesCollected());
        
        // PA3 Requirement 7c - Fireworks information
        System.out.println("Fireworks Planned: " + (hasFireworks ? "Yes" : "No"));
        if (hasFireworks) {
            System.out.printf("Fireworks Cost: $%.2f%n", fireworksCost);
        }
        // PA3 Requirement 7d - Total cost (Venue + Fireworks)
        double totalCost = venue.getCost() + fireworksCost;
        System.out.printf("Venue Cost: $%.2f%n", venue.getCost());
        System.out.printf("Total Cost (Venue + Fireworks): $%.2f%n", totalCost);
        System.out.printf("Expected profit (Sell Out): $%.2f%n", expectedProfit());
        System.out.printf("Actual profit: $%.2f%n", actualProfit());
        System.out.println("=====================");
    }
    
    /**
     * Displays basic event information for customers.
     * Taken from Brian Casio.
     */
    public void displayEventDetailsForCustomer() {
        System.out.println("=====================");
        System.out.println("Event ID: " + id);
        System.out.println(name);
        System.out.println(getDate());
        System.out.println(getTime());
        System.out.println("Event Type: " + getEventType());
        System.out.println("Venue: " + venue.getName());
        System.out.println("=====================");
    }

    // ========== toString Methods ==========
    
    /**
     * String representation of the event.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        return String.format(
            "%s (ID:%s) on %s%nVenue: %s%nPrices: %s",
            name, id, dateTime, venue.getName(), priceMap
        );
    }
}

