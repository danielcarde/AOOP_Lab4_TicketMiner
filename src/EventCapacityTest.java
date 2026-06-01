import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EventCapacityTest {
    private Event testEvent;
    private Venue testVenue;
    private Venue smallVenue;
    private Venue largeVenue;
    
    @Before
    public void setUp() {
        testVenue = new Stadium("Test Stadium", "Test Stadium", "", 100, 5000.0);
        smallVenue = new Stadium("Small", "Small Venue", "", 1, 1000.0);
        largeVenue = new Stadium("Large", "Large Venue", "", 10000, 50000.0);
        
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 75.0);
        prices.put(TicketType.SILVER, 50.0);
        prices.put(TicketType.BRONZE, 30.0);
        prices.put(TicketType.GENERAL_ADMISSION, 20.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        testEvent = new Concert("EVT1", "Test Concert", eventDate, testVenue, prices);
    }
    
    @Test
    public void testRemainingNoSales() {
        int remainingVIP = testEvent.remaining(TicketType.VIP);
        int remainingGold = testEvent.remaining(TicketType.GOLD);
        int remainingSilver = testEvent.remaining(TicketType.SILVER);
        
        assertEquals("VIP remaining should equal capacity", 100, remainingVIP);
        assertEquals("Gold remaining should equal capacity", 100, remainingGold);
        assertEquals("Silver remaining should equal capacity", 100, remainingSilver);
    }
    
    @Test
    public void testRemainingSameForAllTypes() {
        testEvent.sellTickets(TicketType.VIP, 30);
        
        int remainingVIP = testEvent.remaining(TicketType.VIP);
        int remainingGold = testEvent.remaining(TicketType.GOLD);
        int remainingSilver = testEvent.remaining(TicketType.SILVER);
        int remainingBronze = testEvent.remaining(TicketType.BRONZE);
        int remainingGeneral = testEvent.remaining(TicketType.GENERAL_ADMISSION);
        
        assertEquals("All types should return same remaining", 70, remainingVIP);
        assertEquals("All types should return same remaining", 70, remainingGold);
        assertEquals("All types should return same remaining", 70, remainingSilver);
        assertEquals("All types should return same remaining", 70, remainingBronze);
        assertEquals("All types should return same remaining", 70, remainingGeneral);
    }
    
    @Test
    public void testRemainingAllSold() {
        testEvent.sellTickets(TicketType.VIP, 100);
        
        int remaining = testEvent.remaining(TicketType.VIP);
        
        assertEquals("Remaining should be zero when all sold", 0, remaining);
    }
    
    @Test
    public void testRemainingPartiallySold() {
        testEvent.sellTickets(TicketType.GOLD, 45);
        
        int remaining = testEvent.remaining(TicketType.VIP);
        
        assertEquals("Remaining should be capacity - sold", 55, remaining);
    }
    
    @Test
    public void testRemainingMixedTypes() {
        testEvent.sellTickets(TicketType.VIP, 10);
        testEvent.sellTickets(TicketType.GOLD, 15);
        testEvent.sellTickets(TicketType.SILVER, 20);
        
        int remaining = testEvent.remaining(TicketType.BRONZE);
        
        assertEquals("Remaining should account for all types sold", 55, remaining);
    }
    
    @Test
    public void testTotalRemainingNoSales() {
        int remaining = testEvent.totalRemaining();
        
        assertEquals("Total remaining should equal capacity with no sales", 100, remaining);
    }
    
    @Test
    public void testTotalRemainingAllSold() {
        testEvent.sellTickets(TicketType.VIP, 100);
        
        int remaining = testEvent.totalRemaining();
        
        assertEquals("Total remaining should be zero when all sold", 0, remaining);
    }
    
    @Test
    public void testTotalRemainingPartiallySold() {
        testEvent.sellTickets(TicketType.SILVER, 30);
        
        int remaining = testEvent.totalRemaining();
        
        assertEquals("Total remaining should be capacity - sold", 70, remaining);
    }
    
    @Test
    public void testTotalRemainingMixedTypes() {
        testEvent.sellTickets(TicketType.VIP, 5);
        testEvent.sellTickets(TicketType.GOLD, 10);
        testEvent.sellTickets(TicketType.SILVER, 15);
        testEvent.sellTickets(TicketType.BRONZE, 20);
        testEvent.sellTickets(TicketType.GENERAL_ADMISSION, 25);
        
        int remaining = testEvent.totalRemaining();
        
        assertEquals("Total remaining should account for all types", 25, remaining);
    }
    
    @Test
    public void testRemainingEqualsTotalRemaining() {
        testEvent.sellTickets(TicketType.VIP, 40);
        
        int remaining = testEvent.remaining(TicketType.GOLD);
        int totalRemaining = testEvent.totalRemaining();
        
        assertEquals("remaining() should equal totalRemaining()", totalRemaining, remaining);
    }
    
    @Test
    public void testRemainingCapacityOne() {
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 0.0);
        prices.put(TicketType.SILVER, 0.0);
        prices.put(TicketType.BRONZE, 0.0);
        prices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event smallEvent = new Concert("EVT2", "Small Event", eventDate, smallVenue, prices);
        
        assertEquals("Remaining should be 1", 1, smallEvent.totalRemaining());
        
        smallEvent.sellTickets(TicketType.VIP, 1);
        assertEquals("Remaining should be 0", 0, smallEvent.totalRemaining());
    }
    
    @Test
    public void testRemainingLargeCapacity() {
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 0.0);
        prices.put(TicketType.SILVER, 0.0);
        prices.put(TicketType.BRONZE, 0.0);
        prices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event largeEvent = new Concert("EVT3", "Large Event", eventDate, largeVenue, prices);
        
        assertEquals("Remaining should equal capacity", 10000, largeEvent.totalRemaining());
        
        largeEvent.sellTickets(TicketType.VIP, 5000);
        assertEquals("Remaining should be capacity - sold", 5000, largeEvent.totalRemaining());
    }
    
    @Test
    public void testRemainingUpdatesWithSales() {
        assertEquals("Initial remaining", 100, testEvent.totalRemaining());
        
        testEvent.sellTickets(TicketType.VIP, 10);
        assertEquals("After first sale", 90, testEvent.totalRemaining());
        
        testEvent.sellTickets(TicketType.GOLD, 20);
        assertEquals("After second sale", 70, testEvent.totalRemaining());
        
        testEvent.sellTickets(TicketType.SILVER, 30);
        assertEquals("After third sale", 40, testEvent.totalRemaining());
    }
    
    @Test
    public void testRemainingOddCapacity() {
        Venue oddVenue = new Stadium("Odd", "Odd Venue", "", 99, 5000.0);
        
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 0.0);
        prices.put(TicketType.SILVER, 0.0);
        prices.put(TicketType.BRONZE, 0.0);
        prices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event oddEvent = new Concert("EVT4", "Odd Event", eventDate, oddVenue, prices);
        
        oddEvent.sellTickets(TicketType.VIP, 33);
        
        assertEquals("Remaining should be correct", 66, oddEvent.totalRemaining());
    }
}
