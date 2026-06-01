import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EventRevenueTest {
    private Event testEvent;
    private Venue testVenue;
    private Venue expensiveVenue;
    private Venue cheapVenue;
    
    @Before
    public void setUp() {
        testVenue = new Stadium("Test Stadium", "Test Stadium", "", 1000, 5000.0);
        expensiveVenue = new Stadium("Expensive", "Expensive Stadium", "", 1000, 20000.0);
        cheapVenue = new Stadium("Cheap", "Cheap Stadium", "", 1000, 1000.0);
        
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
    public void testTotalRevenueNoSales() {
        double revenue = testEvent.totalRevenue();
        
        assertEquals("Revenue should be zero with no sales", 0.0, revenue, 0.01);
    }
    
    @Test
    public void testTotalRevenueSingleType() {
        testEvent.sellTickets(TicketType.VIP, 10);
        
        double revenue = testEvent.totalRevenue();
        double expectedRevenue = 10 * 100.0;
        
        assertEquals("Revenue should match expected calculation", expectedRevenue, revenue, 0.01);
    }
    
    @Test
    public void testTotalRevenueMultipleTypes() {
        testEvent.sellTickets(TicketType.VIP, 5);
        testEvent.sellTickets(TicketType.GOLD, 10);
        testEvent.sellTickets(TicketType.SILVER, 15);
        testEvent.sellTickets(TicketType.BRONZE, 20);
        testEvent.sellTickets(TicketType.GENERAL_ADMISSION, 30);
        
        double revenue = testEvent.totalRevenue();
        double expectedRevenue = 500.0 + 750.0 + 750.0 + 600.0 + 600.0;
        
        assertEquals("Revenue should sum all ticket types", expectedRevenue, revenue, 0.01);
    }
    
    @Test
    public void testTotalRevenueZeroPrices() {
        Map<TicketType, Double> zeroPrices = new HashMap<>();
        zeroPrices.put(TicketType.VIP, 0.0);
        zeroPrices.put(TicketType.GOLD, 0.0);
        zeroPrices.put(TicketType.SILVER, 0.0);
        zeroPrices.put(TicketType.BRONZE, 0.0);
        zeroPrices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event zeroPriceEvent = new Concert("EVT2", "Free Concert", eventDate, testVenue, zeroPrices);
        
        zeroPriceEvent.sellTickets(TicketType.VIP, 100);
        
        double revenue = zeroPriceEvent.totalRevenue();
        
        assertEquals("Revenue should be zero with zero prices", 0.0, revenue, 0.01);
    }
    
    @Test
    public void testActualProfitPositive() {
        testEvent.sellTickets(TicketType.VIP, 100);
        
        double profit = testEvent.actualProfit();
        double expectedProfit = 10000.0 - 5000.0;
        
        assertEquals("Profit should be positive when revenue > cost", expectedProfit, profit, 0.01);
    }
    
    @Test
    public void testActualProfitNegative() {
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 10.0);
        prices.put(TicketType.GOLD, 10.0);
        prices.put(TicketType.SILVER, 10.0);
        prices.put(TicketType.BRONZE, 10.0);
        prices.put(TicketType.GENERAL_ADMISSION, 10.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event lossEvent = new Concert("EVT3", "Loss Event", eventDate, expensiveVenue, prices);
        
        lossEvent.sellTickets(TicketType.VIP, 50);
        
        double profit = lossEvent.actualProfit();
        double expectedProfit = 500.0 - 20000.0;
        
        assertEquals("Profit should be negative when revenue < cost", expectedProfit, profit, 0.01);
    }
    
    @Test
    public void testActualProfitZero() {
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 0.0);
        prices.put(TicketType.SILVER, 0.0);
        prices.put(TicketType.BRONZE, 0.0);
        prices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event breakEvenEvent = new Concert("EVT4", "Break Even", eventDate, cheapVenue, prices);
        
        breakEvenEvent.sellTickets(TicketType.VIP, 10);
        
        double profit = breakEvenEvent.actualProfit();
        
        assertEquals("Profit should be zero when revenue = cost", 0.0, profit, 0.01);
    }
    
    @Test
    public void testActualProfitNoSales() {
        double profit = testEvent.actualProfit();
        double expectedProfit = 0.0 - 5000.0;
        
        assertEquals("Profit should be negative with no sales", expectedProfit, profit, 0.01);
    }
    
    @Test
    public void testActualProfitZeroVenueCost() {
        Venue freeVenue = new Stadium("Free", "Free Venue", "", 1000, 0.0);
        
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 0.0);
        prices.put(TicketType.SILVER, 0.0);
        prices.put(TicketType.BRONZE, 0.0);
        prices.put(TicketType.GENERAL_ADMISSION, 0.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        Event freeVenueEvent = new Concert("EVT5", "Free Venue Event", eventDate, freeVenue, prices);
        
        freeVenueEvent.sellTickets(TicketType.VIP, 50);
        
        double profit = freeVenueEvent.actualProfit();
        
        assertEquals("Profit should equal revenue with zero venue cost", 5000.0, profit, 0.01);
    }
    
    @Test
    public void testTotalRevenueAccumulation() {
        assertEquals("Initial revenue", 0.0, testEvent.totalRevenue(), 0.01);
        
        testEvent.sellTickets(TicketType.VIP, 5);
        assertEquals("Revenue after first sale", 500.0, testEvent.totalRevenue(), 0.01);
        
        testEvent.sellTickets(TicketType.GOLD, 10);
        assertEquals("Revenue after second sale", 1250.0, testEvent.totalRevenue(), 0.01);
        
        testEvent.sellTickets(TicketType.SILVER, 20);
        assertEquals("Revenue after third sale", 2250.0, testEvent.totalRevenue(), 0.01);
    }
    
    @Test
    public void testActualProfitAccumulation() {
        assertEquals("Initial profit", -5000.0, testEvent.actualProfit(), 0.01);
        
        testEvent.sellTickets(TicketType.VIP, 30);
        assertEquals("Profit after first sale", -2000.0, testEvent.actualProfit(), 0.01);
        
        testEvent.sellTickets(TicketType.GOLD, 20);
        assertEquals("Profit after second sale", -500.0, testEvent.actualProfit(), 0.01);
        
        testEvent.sellTickets(TicketType.SILVER, 10);
        assertEquals("Profit after breaking even", 0.0, testEvent.actualProfit(), 0.01);
        
        testEvent.sellTickets(TicketType.BRONZE, 10);
        assertEquals("Profit after making profit", 300.0, testEvent.actualProfit(), 0.01);
    }
}
