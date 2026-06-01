import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class CustomerChargeTest {
    private Customer customerWithBalance;
    private Customer lowBalanceCustomer;
    private Customer zeroBalanceCustomer;
    
    @Before
    public void setUp() {
        customerWithBalance = new Customer(1, "Rich", "Customer", 500.0, false, "rich", "pass");
        lowBalanceCustomer = new Customer(2, "Low", "Balance", 50.0, false, "low", "pass");
        zeroBalanceCustomer = new Customer(3, "Zero", "Balance", 0.0, false, "zero", "pass");
    }
    
    @Test
    public void testChargeSuccess() {
        double initialBalance = customerWithBalance.getBalance();
        double chargeAmount = 100.0;
        
        boolean result = customerWithBalance.charge(chargeAmount);
        
        assertTrue("Charge should succeed with sufficient balance", result);
        assertEquals("Balance should be deducted", initialBalance - chargeAmount, 
                     customerWithBalance.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeInsufficientBalance() {
        double initialBalance = lowBalanceCustomer.getBalance();
        double chargeAmount = 100.0;
        
        boolean result = lowBalanceCustomer.charge(chargeAmount);
        
        assertFalse("Charge should fail with insufficient balance", result);
        assertEquals("Balance should not change", initialBalance, 
                     lowBalanceCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeExactBalance() {
        Customer exactBalanceCustomer = new Customer(4, "Exact", "Balance", 100.0, false, "exact", "pass");
        double chargeAmount = 100.0;
        
        boolean result = exactBalanceCustomer.charge(chargeAmount);
        
        assertTrue("Charge should succeed with exact balance", result);
        assertEquals("Balance should be zero", 0.0, exactBalanceCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeZeroBalance() {
        double initialBalance = zeroBalanceCustomer.getBalance();
        double chargeAmount = 50.0;
        
        boolean result = zeroBalanceCustomer.charge(chargeAmount);
        
        assertFalse("Charge should fail with zero balance", result);
        assertEquals("Balance should remain zero", initialBalance, 
                     zeroBalanceCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeZeroAmount() {
        double initialBalance = customerWithBalance.getBalance();
        
        boolean result = customerWithBalance.charge(0.0);
        
        assertTrue("Charge should succeed with zero amount", result);
        assertEquals("Balance should not change", initialBalance, 
                     customerWithBalance.getBalance(), 0.01);
    }
    
    @Test
    public void testMultipleCharges() {
        Customer multiChargeCustomer = new Customer(5, "Multi", "Charge", 300.0, false, "multi", "pass");
        
        boolean result1 = multiChargeCustomer.charge(100.0);
        assertTrue("First charge should succeed", result1);
        assertEquals("Balance after first charge", 200.0, 
                     multiChargeCustomer.getBalance(), 0.01);
        
        boolean result2 = multiChargeCustomer.charge(75.0);
        assertTrue("Second charge should succeed", result2);
        assertEquals("Balance after second charge", 125.0, 
                     multiChargeCustomer.getBalance(), 0.01);
        
        boolean result3 = multiChargeCustomer.charge(50.0);
        assertTrue("Third charge should succeed", result3);
        assertEquals("Balance after third charge", 75.0, 
                     multiChargeCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeToZero() {
        Customer testCustomer = new Customer(6, "Test", "Customer", 150.0, false, "test", "pass");
        
        boolean result = testCustomer.charge(150.0);
        
        assertTrue("Charge should succeed", result);
        assertEquals("Balance should be zero", 0.0, testCustomer.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeSmallAmount() {
        double initialBalance = customerWithBalance.getBalance();
        double chargeAmount = 0.01;
        
        boolean result = customerWithBalance.charge(chargeAmount);
        
        assertTrue("Charge should succeed with small amount", result);
        assertEquals("Balance should be deducted correctly", initialBalance - chargeAmount, 
                     customerWithBalance.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeLargeAmount() {
        double initialBalance = customerWithBalance.getBalance();
        double chargeAmount = 10000.0;
        
        boolean result = customerWithBalance.charge(chargeAmount);
        
        assertFalse("Charge should fail with large amount exceeding balance", result);
        assertEquals("Balance should not change", initialBalance, 
                     customerWithBalance.getBalance(), 0.01);
    }
    
    @Test
    public void testChargeSequenceSuccessThenFailure() {
        Customer testCustomer = new Customer(7, "Seq", "Test", 100.0, false, "seq", "pass");
        
        boolean result1 = testCustomer.charge(60.0);
        assertTrue("First charge should succeed", result1);
        assertEquals("Balance after first charge", 40.0, testCustomer.getBalance(), 0.01);
        
        boolean result2 = testCustomer.charge(50.0);
        assertFalse("Second charge should fail", result2);
        assertEquals("Balance should remain unchanged", 40.0, testCustomer.getBalance(), 0.01);
    }
}
