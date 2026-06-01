import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class CustomerAuthenticationTest {
    private Customer testCustomer;
    
    @Before
    public void setUp() {
        testCustomer = new Customer(1, "John", "Doe", 100.0, false, "john_doe", "secret123");
    }
    
    @Test
    public void testAuthenticateCorrectCredentials() {
        boolean result = testCustomer.authenticate("john_doe", "secret123");
        
        assertTrue("Authentication should succeed with correct credentials", result);
    }
    
    @Test
    public void testAuthenticateWrongPassword() {
        boolean result = testCustomer.authenticate("john_doe", "wrong_password");
        
        assertFalse("Authentication should fail with wrong password", result);
    }
    
    @Test
    public void testAuthenticateWrongUsername() {
        boolean result = testCustomer.authenticate("wrong_user", "secret123");
        
        assertFalse("Authentication should fail with wrong username", result);
    }
    
    @Test
    public void testAuthenticateBothWrong() {
        boolean result = testCustomer.authenticate("wrong_user", "wrong_password");
        
        assertFalse("Authentication should fail with both credentials wrong", result);
    }
    
    @Test
    public void testAuthenticateUsernameCaseSensitive() {
        boolean result = testCustomer.authenticate("John_Doe", "secret123");
        
        assertFalse("Authentication should fail with different case username", result);
    }
    
    @Test
    public void testAuthenticatePasswordCaseSensitive() {
        boolean result = testCustomer.authenticate("john_doe", "Secret123");
        
        assertFalse("Authentication should fail with different case password", result);
    }
    
    @Test
    public void testAuthenticateEmptyUsername() {
        boolean result = testCustomer.authenticate("", "secret123");
        
        assertFalse("Authentication should fail with empty username", result);
    }
    
    @Test
    public void testAuthenticateEmptyPassword() {
        boolean result = testCustomer.authenticate("john_doe", "");
        
        assertFalse("Authentication should fail with empty password", result);
    }
    
    @Test
    public void testCheckPasswordCorrect() {
        boolean result = testCustomer.checkPassword("secret123");
        
        assertTrue("checkPassword should succeed with correct password", result);
    }
    
    @Test
    public void testCheckPasswordIncorrect() {
        boolean result = testCustomer.checkPassword("wrong_password");
        
        assertFalse("checkPassword should fail with incorrect password", result);
    }
    
    @Test
    public void testCheckPasswordCaseSensitive() {
        boolean result = testCustomer.checkPassword("Secret123");
        
        assertFalse("checkPassword should fail with different case password", result);
    }
    
    @Test
    public void testCheckPasswordEmpty() {
        boolean result = testCustomer.checkPassword("");
        
        assertFalse("checkPassword should fail with empty password", result);
    }
    
    @Test
    public void testCheckPasswordWithWhitespace() {
        boolean result = testCustomer.checkPassword("secret123 ");
        
        assertFalse("checkPassword should fail with whitespace in password", result);
    }
    
    @Test
    public void testAuthenticateSpecialCharacters() {
        Customer specialCustomer = new Customer(2, "Special", "User", 100.0, false, 
                                                "user@123", "pass!@#$");
        
        boolean result = specialCustomer.authenticate("user@123", "pass!@#$");
        
        assertTrue("Authentication should succeed with special characters", result);
    }
    
    @Test
    public void testAuthenticateLongCredentials() {
        String longUsername = "a".repeat(100);
        String longPassword = "b".repeat(100);
        Customer longCustomer = new Customer(3, "Long", "User", 100.0, false, 
                                             longUsername, longPassword);
        
        boolean result = longCustomer.authenticate(longUsername, longPassword);
        
        assertTrue("Authentication should succeed with long credentials", result);
    }
    
    @Test
    public void testMultipleAuthenticationAttempts() {
        boolean result1 = testCustomer.authenticate("john_doe", "secret123");
        assertTrue("First authentication should succeed", result1);
        
        boolean result2 = testCustomer.authenticate("john_doe", "wrong");
        assertFalse("Second authentication should fail", result2);
        
        boolean result3 = testCustomer.authenticate("john_doe", "secret123");
        assertTrue("Third authentication should succeed", result3);
    }
}
