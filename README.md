# TicketMiner — PA4

Team Java coursework extending a ticketing system with Swing interfaces, event inventory, membership discounts, fees, cancellation, and reporting. Source comments retain the original contributor credits; this repository does not claim sole authorship of the team implementation.

## Build and test

Requires a JDK (CI uses Java 21). JUnit 4.13.2 and Hamcrest 1.3 are included in `lib`.

```sh
mkdir -p build
javac -cp 'lib/*' -d build src/*.java
java -cp 'build:lib/*' org.junit.runner.JUnitCore CustomerAuthenticationTest CustomerChargeTest CustomerPurchaseTest EventCapacityTest EventRevenueTest EventTest InvoiceTest
```

On Windows use `;` instead of `:` in the runtime classpath. The CI workflow discovers all `*Test.java` classes instead of relying on the example list; use the actual test filenames in your checkout. All production sources compile and the current seven test classes pass 69 checks on JDK 25 locally.

## Compatibility repairs

The PA4 integration retains the legacy CSV Customer constructor and compatibility accessors, uses the existing static Logger API, and sorts String event IDs with a String comparator. The stored legacy concert count is CSV compatibility data; it is not a complete reconstructed purchase history.

Invoice tests include PA4 convenience, service, and charity fees before the membership discount and tax. Percentage rounding uses decimal arithmetic to preserve the existing round-down policy at exact cents. The membership discount remains an unrounded double; this educational billing model is not intended for production payments.

## Data and limitations

The repository owner confirms the customer/account fixtures are synthetic teaching data. Credentials in educational CSV fixtures are plaintext examples; do not use real customer records or passwords. Console/Swing flows, CSV persistence, and cancellation behavior need broader end-to-end validation. Passing unit checks do not establish production readiness.

See source entry points and the assignment requirements in `src` for the original coursework context. No external authentication service or payment processor is connected.
