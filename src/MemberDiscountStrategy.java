/**
 * Member Discount Strategy - 10% discount for TicketMiner Members.
 * PA3 Requirement 5 - Strategy Pattern Implementation.
 * 
 * This strategy provides a 10% discount on non-VIP tickets for members.
 */
public class MemberDiscountStrategy implements DiscountStrategy {
    private static final double DISCOUNT_PERCENTAGE = 10.0;
    
    @Override
    public double calculateDiscount(double subtotal, TicketType ticketType) {
        // VIP tickets are not eligible for member discount
        if (ticketType == TicketType.VIP) {
            return 0.0;
        }
        // 10% discount on subtotal
        return subtotal * (DISCOUNT_PERCENTAGE / 100.0);
    }
    
    @Override
    public double getDiscountPercentage() {
        return DISCOUNT_PERCENTAGE;
    }
    
    @Override
    public boolean appliesTo(TicketType ticketType) {
        // Discount applies to all ticket types except VIP
        return ticketType != TicketType.VIP;
    }
}



