import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JUnit test cases for Event class.
 * PA3 Requirement 13 - Test cases for at least two methods.
 * 
 * Tests the ticket selling functionality.
 */
public class EventTest {
    private Event testEvent;
    private Venue testVenue;
    
    @Before
    public void setUp() {
        // Create test venue with capacity of 100
        testVenue = new Stadium("Test Stadium", "Test Stadium", "", 100, 5000.0);
        
        // Create test event with prices
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 75.0);
        prices.put(TicketType.SILVER, 50.0);
        prices.put(TicketType.BRONZE, 30.0);
        prices.put(TicketType.GENERAL_ADMISSION, 20.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        testEvent = new Concert("TEST1", "Test Concert", eventDate, testVenue, prices);
    }
    
    /**
     * Test successful ticket selling.
     * Tests sellTickets() method.
     */
    @Test
    public void testSellTicketsSuccess() {
        // Initial state: no tickets sold
        assertEquals(100, testEvent.totalRemaining());
        
        // Sell 10 VIP tickets
        boolean result = testEvent.sellTickets(TicketType.VIP, 10);
        
        assertTrue(result);
        assertEquals(90, testEvent.totalRemaining());
        assertEquals(10, testEvent.sold(TicketType.VIP));
        
        // Revenue should be 10 * 100.0 = 1000.0
        assertEquals(1000.0, testEvent.revenue(TicketType.VIP), 0.01);
    }
    
    /**
     * Test selling tickets beyond capacity.
     * Tests sellTickets() method with invalid quantity.
     */
    @Test
    public void testSellTicketsExceedsCapacity() {
        // Try to sell 101 tickets (exceeds capacity of 100)
        boolean result = testEvent.sellTickets(TicketType.GOLD, 101);
        
        assertFalse(result);
        assertEquals(100, testEvent.totalRemaining());
        assertEquals(0, testEvent.sold(TicketType.GOLD));
    }
    
    /**
     * Test selling multiple ticket types.
     */
    @Test
    public void testSellMultipleTicketTypes() {
        // Sell different types of tickets
        testEvent.sellTickets(TicketType.VIP, 5);
        testEvent.sellTickets(TicketType.GOLD, 10);
        testEvent.sellTickets(TicketType.SILVER, 15);
        testEvent.sellTickets(TicketType.BRONZE, 20);
        testEvent.sellTickets(TicketType.GENERAL_ADMISSION, 30);
        
        // Total sold: 5 + 10 + 15 + 20 + 30 = 80
        assertEquals(20, testEvent.totalRemaining());
        assertEquals(5, testEvent.sold(TicketType.VIP));
        assertEquals(10, testEvent.sold(TicketType.GOLD));
        assertEquals(15, testEvent.sold(TicketType.SILVER));
        assertEquals(20, testEvent.sold(TicketType.BRONZE));
        assertEquals(30, testEvent.sold(TicketType.GENERAL_ADMISSION));
        
        // Check total revenue
        double expectedRevenue = (5 * 100.0) + (10 * 75.0) + (15 * 50.0) + (20 * 30.0) + (30 * 20.0);
        assertEquals(expectedRevenue, testEvent.totalRevenue(), 0.01);
    }
    
    /**
     * Test remaining() method.
     */
    @Test
    public void testRemainingTickets() {
        testEvent.sellTickets(TicketType.VIP, 10);
        
        // remaining() should return total remaining (not type-specific)
        assertEquals(90, testEvent.remaining(TicketType.VIP));
        assertEquals(90, testEvent.remaining(TicketType.GOLD));
        assertEquals(90, testEvent.totalRemaining());
    }
    
    /**
     * Test revenue tracking per ticket type.
     */
    @Test
    public void testRevenueTracking() {
        testEvent.sellTickets(TicketType.GOLD, 5);
        testEvent.sellTickets(TicketType.SILVER, 3);
        
        // Gold revenue: 5 * 75.0 = 375.0
        assertEquals(375.0, testEvent.revenue(TicketType.GOLD), 0.01);
        
        // Silver revenue: 3 * 50.0 = 150.0
        assertEquals(150.0, testEvent.revenue(TicketType.SILVER), 0.01);
        
        // Total revenue: 375.0 + 150.0 = 525.0
        assertEquals(525.0, testEvent.totalRevenue(), 0.01);
    }
}



