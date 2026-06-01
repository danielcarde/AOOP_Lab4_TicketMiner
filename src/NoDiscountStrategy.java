/**
 * No Discount Strategy - No discount applied.
 * PA3 Requirement 5 - Strategy Pattern Implementation.
 * 
 * This is the default strategy when no discount applies.
 */
public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double subtotal, TicketType ticketType) {
        return 0.0;
    }
    
    @Override
    public double getDiscountPercentage() {
        return 0.0;
    }
    
    @Override
    public boolean appliesTo(TicketType ticketType) {
        return false;
    }
}



