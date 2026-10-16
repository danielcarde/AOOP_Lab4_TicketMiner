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
        
        // PA4: include convenience, service and charity fees before discount and tax.
        assertEquals(154.37, invoice.getSubtotal(), 0.001);
        assertEquals(15.437, invoice.getMemberDiscount(), 0.001);
        assertEquals(138.933, invoice.getDiscountedSubtotal(), 0.001);
        assertTrue(invoice.hadMemberDiscount());
        assertEquals(150.393, invoice.getTotalPrice(), 0.001);
    }
    
    /**
     * Test that VIP tickets do not get member discount.
     * Tests Strategy Pattern - MemberDiscountStrategy.appliesTo().
     */
    @Test
    public void testVIPNoMemberDiscount() {
        // Create invoice for member with VIP ticket (should NOT get discount)
        Invoice invoice = new Invoice(memberCustomer, testEvent, TicketType.VIP, 1);
        
        // PA4: include convenience, service and charity fees before discount and tax.
        assertEquals(103.75, invoice.getSubtotal(), 0.001);
        assertEquals(0.0, invoice.getMemberDiscount(), 0.01);
        assertFalse(invoice.hadMemberDiscount());
        assertEquals(112.30, invoice.getTotalPrice(), 0.001);
    }
    
    /**
     * Test discount calculation for non-member customer.
     * Tests NoDiscountStrategy implementation.
     */
    @Test
    public void testNonMemberNoDiscount() {
        // Create invoice for non-member (should get no discount)
        Invoice invoice = new Invoice(nonMemberCustomer, testEvent, TicketType.SILVER, 3);
        
        // PA4: include convenience, service and charity fees before discount and tax.
        assertEquals(154.37, invoice.getSubtotal(), 0.001);
        assertEquals(0.0, invoice.getMemberDiscount(), 0.01);
        assertFalse(invoice.hadMemberDiscount());
        assertEquals(167.10, invoice.getTotalPrice(), 0.001);
    }
    
    /**
     * Test sales tax calculation.
     */
    @Test
    public void testSalesTaxCalculation() {
        Invoice invoice = new Invoice(nonMemberCustomer, testEvent, TicketType.BRONZE, 4);
        
        // PA4: include convenience, service and charity fees before discount and tax.
        assertEquals(124.0, invoice.getSubtotal(), 0.001);
        assertEquals(0.90, invoice.getCharityFee(), 0.001);
        assertEquals(4.00, invoice.getTotalFees(), 0.001);
        assertEquals(10.23, invoice.getSalesTax(), 0.001);
        assertEquals(134.23, invoice.getTotalPrice(), 0.001);
    }
}



