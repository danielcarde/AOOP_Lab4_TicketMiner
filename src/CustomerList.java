/**
 * CustomerList class - Taken from teamgd (Daniel and Gabriel)
 * This is an alternative singleton implementation for managing customers.
 * Note: This uses a different structure than DataStore from teammb.
 * The base code uses DataStore, but this class is included as an alternative from teamgd.
 */

import java.util.*;
import java.io.*;

public final class CustomerList {
    private static volatile CustomerList instance;
    private Map<String, Customer> customerList;
    private static final String fileName = "CustomerListUpdated.csv";

    private CustomerList() {
        customerList = new HashMap<>();
    }

    public static CustomerList getInstance() {
        if (instance == null) {
            synchronized (CustomerList.class) {
                if (instance == null) {
                    instance = new CustomerList();
                }
            }
        }
        return instance;
    }

    public void loadCustomersFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("CSV file is empty.");
                return;
            }

            String[] headers = splitCSVLine(headerLine);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }

            String currLine;
            while ((currLine = br.readLine()) != null) {
                if (currLine.isBlank())
                    continue;

                String[] details = splitCSVLine(currLine);

                try {
                    String username = getValue(details, headerMap, "username", "");
                    String password = getValue(details, headerMap, "password", "");

                    if (username.isEmpty() || password.isEmpty()) {
                        continue;
                    }

                    int id = Integer.parseInt(getValue(details, headerMap, "id", "0"));
                    String firstName = getValue(details, headerMap, "first name", "");
                    String lastName = getValue(details, headerMap, "last name", "");
                    double moneyAvailable = Double.parseDouble(getValue(details, headerMap, "money available", "0"));
                    int concertsPurchased = Integer.parseInt(getValue(details, headerMap, "concerts purchased", "0"));
                    boolean hasMembership = Boolean
                            .parseBoolean(getValue(details, headerMap, "ticketminer membership", "false"));

                    Customer customer = new Customer(id, firstName, lastName, username, password, moneyAvailable,
                            concertsPurchased, hasMembership);
                    customerList.put(username, customer);

                } catch (Exception e) {
                    System.out.println("Error Loading Customers.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Customer> getCustomerList() {
        return customerList;
    }

    public Customer searchCustomerByUsername(String username) {
        return customerList.getOrDefault(username, null);
    }

    public void writeCustomersToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(
                    "ID,First Name,Last Name,Money Available,Money Saved,Concerts Purchased,TicketMiner Membership,Username,Password");
            bw.newLine();

            List<Customer> customers = new ArrayList<>(customerList.values());
            customers.sort(Comparator.comparingInt(Customer::getId));

            for (Customer customer : customers) {
                bw.write(customer.getId() + "," +
                        customer.getFirstName() + "," +
                        customer.getLastName() + "," +
                        customer.getMoneyAvailable() + "," +
                        customer.getSavedMoney() + "," +
                        customer.getConcertsPurchased() + "," +
                        customer.isTicketMinerMembership() + "," +
                        customer.getUsername() + "," +
                        customer.getPassword());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] splitCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }

    private String getValue(String[] details, Map<String, Integer> headerMap, String column, String defaultValue) {
        Integer i = headerMap.get(column.toLowerCase());
        if (i == null || i >= details.length || details[i].trim().isEmpty()) {
            return defaultValue;
        }
        return details[i].trim();
    }

    public void writeCustomerLatestReceipt(String username) {
        Customer customer = searchCustomerByUsername(username);
        if (customer == null) {
            return;
        }
        List<Invoice> invoices = customer.getInvoices();
        if (invoices == null || invoices.isEmpty()) {
            return;
        }

        Invoice latest = invoices.get(invoices.size() - 1);
        String filename = username + "_Receipt.txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(latest.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCustomerInvoiceSummary(String username) {
        Customer customer = searchCustomerByUsername(username);
        if (customer == null)
            return;

        List<Invoice> invoices = customer.getInvoices();
        if (invoices == null || invoices.isEmpty())
            return;

        String filename = username + "_InvoiceSummary.txt";

        try (FileWriter writer = new FileWriter(filename)) {
            for (Invoice inv : invoices) {
                writer.write(inv.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewCustomerReceipt(String username, int invoiceIndex) {
        Customer customer = searchCustomerByUsername(username);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        List<Invoice> invoices = customer.getInvoices();
        if (invoices == null || invoices.isEmpty() || invoiceIndex < 0 || invoiceIndex >= invoices.size()) {
            System.out.println("No receipts found for this customer.");
            return;
        }

        Invoice inv = invoices.get(invoiceIndex);
        System.out.println(inv.toString());
    }
}

