import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomerPurchaseTest {
    private Customer memberCustomer;
    private Customer nonMemberCustomer;
    private Customer lowBalanceCustomer;
    private Event testEvent;
    private Venue testVenue;
    
    @Before
    public void setUp() {
        testVenue = new Stadium("Test Stadium", "Test Stadium", "", 1000, 10000.0);
        
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, 100.0);
        prices.put(TicketType.GOLD, 75.0);
        prices.put(TicketType.SILVER, 50.0);
        prices.put(TicketType.BRONZE, 30.0);
        prices.put(TicketType.GENERAL_ADMISSION, 20.0);
        
        LocalDateTime eventDate = LocalDateTime.of(2025, 12, 25, 19, 0);
        testEvent = new Concert("EVT1", "Test Concert", eventDate, testVenue, prices);
        
        memberCustomer = new Customer(1, "John", "Member", 500.0, true, "john_member", "pass123");
        nonMemberCustomer = new Customer(2, "Jane", "NonMember", 500.0, false, "jane_user", "pass456");
        lowBalanceCustomer = new Customer(3, "Bob", "LowBalance", 50.0, false, "bob_user", "pass789");
    }
    
    @Test
    public void testPurchaseTicketSuccess() {
        double initialBalance = memberCustomer.getBalance();
        double purchaseAmount = 100.0;
        int initialInvoiceCount = memberCustomer.getInvoices().size();
        
        boolean result = memberCustomer.purchaseTicket(purchaseAmount, testEvent, "VIP", 1);
        
        assertTrue("Purchase should succeed with sufficient balance", result);
        assertEquals("Balance should be deducted", initialBalance - purchaseAmount, 
                     memberCustomer.getBalance(), 0.01);
        assertEquals("Invoice should be added", initialInvoiceCount + 1, 
                     memberCustomer.getInvoices().size());
    }
    
    @Test
    public void testPurchaseTicketInsufficientBalance() {
        double initialBalance = lowBalanceCustomer.getBalance();
        double purchaseAmount = 100.0;
        int initialInvoiceCount = lowBalanceCustomer.getInvoices().size();
        
        boolean result = lowBalanceCustomer.purchaseTicket(purchaseAmount, testEvent, "GOLD", 1);
        
        assertFalse("Purchase should fail with insufficient balance", result);
        assertEquals("Balance should not change", initialBalance,
                     lowBalanceCustomer.getBalance(), 0.01);
        assertEquals("No invoice should be added", initialInvoiceCount, 
                     lowBalanceCustomer.getInvoices().size());
    }
    
    @Test
    public void testPurchaseTicketExactBalance() {
        Customer exactBalanceCustomer = new Customer(4, "Exact", "Balance", 100.0, false, "exact", "pass");
        double purchaseAmount = 100.0;
        
        boolean result = exactBalanceCustomer.purchaseTicket(purchaseAmount, testEvent, "VIP", 1);
        
        assertTrue("Purchase should succeed with exact balance", result);
        assertEquals("Balance should be zero", 0.0, exactBalanceCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testPurchaseTicketZeroBalance() {
        Customer zeroBalanceCustomer = new Customer(5, "Zero", "Balance", 0.0, false, "zero", "pass");
        double purchaseAmount = 50.0;
        int initialInvoiceCount = zeroBalanceCustomer.getInvoices().size();
        
        boolean result = zeroBalanceCustomer.purchaseTicket(purchaseAmount, testEvent, "SILVER", 1);
        
        assertFalse("Purchase should fail with zero balance", result);
        assertEquals("Balance should remain zero", 0.0, zeroBalanceCustomer.getBalance(), 0.01);
        assertEquals("No invoice should be added", initialInvoiceCount, 
                     zeroBalanceCustomer.getInvoices().size());
    }
    
    @Test
    public void testPurchaseTicketInvalidType() {
        double initialBalance = memberCustomer.getBalance();
        double purchaseAmount = 100.0;
        int initialInvoiceCount = memberCustomer.getInvoices().size();
        
        boolean result = memberCustomer.purchaseTicket(purchaseAmount, testEvent, "INVALID_TYPE", 1);
        
        assertFalse("Purchase should fail with invalid ticket type", result);
        assertEquals("Balance is deducted before validation, so it changes", initialBalance - purchaseAmount, 
                     memberCustomer.getBalance(), 0.01);
        assertEquals("No invoice should be added", initialInvoiceCount, 
                     memberCustomer.getInvoices().size());
    }
    
    @Test
    public void testPurchaseTicketGeneralType() {
        double initialBalance = nonMemberCustomer.getBalance();
        double purchaseAmount = 20.0;
        
        boolean result = nonMemberCustomer.purchaseTicket(purchaseAmount, testEvent, "General", 1);
        
        assertTrue("Purchase should succeed with 'General' type", result);
        assertEquals("Balance should be deducted", initialBalance - purchaseAmount, 
                     nonMemberCustomer.getBalance(), 0.01);
    }
    
    @Test(expected = NullPointerException.class)
    public void testPurchaseTicketNullEvent() {
        double purchaseAmount = 100.0;
        
        memberCustomer.purchaseTicket(purchaseAmount, null, "VIP", 1);
    }
    
    @Test
    public void testPurchaseTicketAddsToEvent() {
        int initialEventTicketCount = testEvent.getTicketCount();
        double purchaseAmount = 100.0;
        
        boolean result = memberCustomer.purchaseTicket(purchaseAmount, testEvent, "VIP", 1);
        
        assertTrue("Purchase should succeed", result);
        assertEquals("Event should have new ticket purchase", 
                     initialEventTicketCount + 1, testEvent.getTicketCount());
    }
    
    @Test
    public void testMultiplePurchases() {
        Customer multiPurchaseCustomer = new Customer(6, "Multi", "Purchase", 300.0, false, "multi", "pass");
        
        boolean result1 = multiPurchaseCustomer.purchaseTicket(100.0, testEvent, "VIP", 1);
        assertTrue("First purchase should succeed", result1);
        assertEquals("Balance after first purchase", 200.0, 
                     multiPurchaseCustomer.getBalance(), 0.01);
        
        boolean result2 = multiPurchaseCustomer.purchaseTicket(75.0, testEvent, "GOLD", 1);
        assertTrue("Second purchase should succeed", result2);
        assertEquals("Balance after second purchase", 125.0, 
                     multiPurchaseCustomer.getBalance(), 0.01);
        
        boolean result3 = multiPurchaseCustomer.purchaseTicket(50.0, testEvent, "SILVER", 1);
        assertTrue("Third purchase should succeed", result3);
        assertEquals("Balance after third purchase", 75.0, 
                     multiPurchaseCustomer.getBalance(), 0.01);
        
        assertEquals("Should have 3 invoices", 3, multiPurchaseCustomer.getInvoices().size());
    }
}

