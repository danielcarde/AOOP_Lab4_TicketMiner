import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite for TicketMiner System.
 * PA3 Requirement 13 - Test suite to run all test cases.
 * 
 * This suite runs all test classes together.
 */
@RunWith(Suite.class)
@SuiteClasses({
    InvoiceTest.class,
    EventTest.class
})
public class TestSuite {
    // This class remains empty, used only as a holder for the above annotations
}



