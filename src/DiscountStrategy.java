/**
 * Strategy Pattern Interface for discount calculations.
 * PA3 Requirement 5 - Second Design Pattern.
 * 
 * This interface defines the contract for different discount strategies.
 * Implementations can provide different discount calculation methods.
 */
public interface DiscountStrategy {
    /**
     * Calculates the discount amount for a given subtotal.
     * 
     * @param subtotal The original subtotal before discount
     * @param ticketType The type of ticket being purchased
     * @return The discount amount to apply
     */
    double calculateDiscount(double subtotal, TicketType ticketType);
    
    /**
     * Gets the discount percentage (for display purposes).
     * 
     * @return The discount percentage (e.g., 10.0 for 10%)
     */
    double getDiscountPercentage();
    
    /**
     * Checks if this discount strategy applies to the given ticket type.
     * 
     * @param ticketType The ticket type to check
     * @return true if discount applies, false otherwise
     */
    boolean appliesTo(TicketType ticketType);
}



