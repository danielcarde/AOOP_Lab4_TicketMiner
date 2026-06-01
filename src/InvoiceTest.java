import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JUnit test cases for Invoice class.
 * PA3 Requirement 13 - Test cases for at least two methods.
 * 
 * Tests the discount calculation functionality using Strategy Pattern.
 */
public class InvoiceTest {
    private Customer memberCustomer;
    private Customer nonMemberCustomer;
    private Event testEvent;
    private Venue testVenue;
    
    @Before
    public void setUp() {
        // Create test venue
        testVenue = new Stadium("Test Stadium", "Test Stadium", "", 10000, 5000.0);
        
        // Create test event with prices
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 75.0);
        prices.put(TicketType.SILVER, 50.0);
        prices.put(TicketType.BRONZE, 30.0);
        prices.put(TicketType.GENERAL_ADMISSION, 20.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        testEvent = new Concert("TEST1", "Test Concert", eventDate, testVenue, prices);
        
        // Create test customers
        memberCustomer = new Customer(1, "Test", "Member", 1000.0, true, "testmember", "password");
        nonMemberCustomer = new Customer(2, "Test", "NonMember", 1000.0, false, "testnonmember", "password");
    }
    
    /**
     * Test discount calculation for member customer with non-VIP ticket.
     * Tests Strategy Pattern implementation.
     */
    @Test
    public void testMemberDiscountCalculation() {
        // Create invoice for member with GOLD ticket (should get 10% discount)
        Invoice invoice = new Invoice(memberCustomer, testEvent, TicketType.GOLD, 2);
        
        // Subtotal: 75.0 * 2 = 150.0
        // Member discount (10%): 150.0 * 0.10 = 15.0
        // Discounted subtotal: 150.0 - 15.0 = 135.0
        // Tax (8.25%): 135.0 * 0.0825 = 11.1375 (rounded down to 11.13)
        // Total: 135.0 + 11.13 = 146.13
        
        assertEquals(150.0, invoice.getSubtotal(), 0.01);
        assertEquals(15.0, invoice.getMemberDiscount(), 0.01);
        assertEquals(135.0, invoice.getDiscountedSubtotal(), 0.01);
        assertTrue(invoice.hadMemberDiscount());
        assertEquals(146.13, invoice.getTotalPrice(), 0.01);
    }
    
    /**
     * Test that VIP tickets do not get member discount.
     * Tests Strategy Pattern - MemberDiscountStrategy.appliesTo().
     */
    @Test
    public void testVIPNoMemberDiscount() {
        // Create invoice for member with VIP ticket (should NOT get discount)
        Invoice invoice = new Invoice(memberCustomer, testEvent, TicketType.VIP, 1);
        
        // Subtotal: 100.0 * 1 = 100.0
        // Member discount: 0.0 (VIP excluded)
        // Discounted subtotal: 100.0
        // Tax (8.25%): 100.0 * 0.0825 = 8.25
        // Total: 100.0 + 8.25 = 108.25
        
        assertEquals(100.0, invoice.getSubtotal(), 0.01);
        assertEquals(0.0, invoice.getMemberDiscount(), 0.01);
        assertFalse(invoice.hadMemberDiscount());
        assertEquals(108.25, invoice.getTotalPrice(), 0.01);
    }
    
    /**
     * Test discount calculation for non-member customer.
     * Tests NoDiscountStrategy implementation.
     */
    @Test
    public void testNonMemberNoDiscount() {
        // Create invoice for non-member (should get no discount)
        Invoice invoice = new Invoice(nonMemberCustomer, testEvent, TicketType.SILVER, 3);
        
        // Subtotal: 50.0 * 3 = 150.0
        // Member discount: 0.0
        // Discounted subtotal: 150.0
        // Tax (8.25%): 150.0 * 0.0825 = 12.375 (rounded down to 12.37)
        // Total: 150.0 + 12.37 = 162.37
        
        assertEquals(150.0, invoice.getSubtotal(), 0.01);
        assertEquals(0.0, invoice.getMemberDiscount(), 0.01);
        assertFalse(invoice.hadMemberDiscount());
        assertEquals(162.37, invoice.getTotalPrice(), 0.01);
    }
    
    /**
     * Test sales tax calculation.
     */
    @Test
    public void testSalesTaxCalculation() {
        Invoice invoice = new Invoice(nonMemberCustomer, testEvent, TicketType.BRONZE, 4);
        
        // Subtotal: 30.0 * 4 = 120.0
        // Tax (8.25%): 120.0 * 0.0825 = 9.90
        // Total: 120.0 + 9.90 = 129.90
        
        assertEquals(120.0, invoice.getSubtotal(), 0.01);
        assertEquals(9.90, invoice.getSalesTax(), 0.01);
        assertEquals(129.90, invoice.getTotalPrice(), 0.01);
    }
}



