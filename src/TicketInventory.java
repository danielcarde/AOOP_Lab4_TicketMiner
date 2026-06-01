/**
 * Interface defining operations for managing ticket inventory and revenue tracking.
 * 
 * Taken from Miranda Macias
 * This interface provides a contract for classes that need to manage ticket sales,
 * track remaining tickets, and calculate revenue and profits.
 */
public interface TicketInventory {

    /**
     * Attempts to sell the specified quantity of tickets of the given type.
     * 
     * @param type The type of ticket to sell
     * @param qty The quantity of tickets to sell
     * @return true if the tickets were successfully sold, false otherwise
     */
    boolean sellTickets(TicketType type, int qty);
    
    /**
     * Gets the number of remaining tickets of the specified type.
     * 
     * @param type The type of ticket to check
     * @return The number of remaining tickets of that type
     */
    int remaining(TicketType type);
    
    /**
     * Gets the total number of remaining tickets across all types.
     * 
     * @return The total number of remaining tickets
     */
    int totalRemaining();
    
    /**
     * Calculates the total revenue from tickets of the specified type.
     * 
     * @param type The type of ticket to calculate revenue for
     * @return The total revenue from that ticket type
     */
    double revenue(TicketType type);
    
    /**
     * Calculates the total revenue from all ticket sales.
     * 
     * @return The total revenue from all ticket types
     */
    double totalRevenue();
    
    /**
     * Calculates the expected profit if all tickets are sold at their listed prices.
     * 
     * @return The expected profit at sell-out
     */
    double expectedProfit();
    
    /**
     * Calculates the actual profit based on current ticket sales.
     * 
     * @return The actual profit from current sales
     */
    double actualProfit(); 
}

