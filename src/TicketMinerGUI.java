/**
 * The University of Texas at El Paso
 * Department of Computer Science
 * CS 3331 – Advanced Object-Oriented Programming
 * Instructor: Daniel Mejia
 * Fall 2025
 * Programming Assignment 4
 * 
 * Name: Miranda Macias, Brian Casio (teammb)
 * 
 * Lab Description: This program implements a GUI version of the TicketMiner ticket purchasing system
 * using Java Swing. It maintains all functionalities from RunTicket.java, allowing customers to browse
 * events, purchase tickets with various fees, cancel purchases, and generate receipts. Administrators
 * can manage events, view revenue reports, and process automatic purchases.
 * 
 * Honesty Statement: We declare that this submission is the result of our own work and has
 * been completed in collaboration with our team members. We have not given or received
 * unauthorized assistance on this assignment.
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GUI version of TicketMiner using Java Swing.
 * Maintains all functionalities from RunTicket.java
 * 
 * @author GUI Implementation
 */
public class TicketMinerGUI extends JFrame {
    private DataStore ds;
    private Customer currentCustomer;
    private String dataVersion = "PA4";
    private String basePath = "";
    
    // Main panels
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel customerPanel;
    private JPanel adminPanel;
    
    // Card layout for switching panels
    private CardLayout cardLayout;
    
    public TicketMinerGUI() {
        super("Ticket Miner - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Determine base path
        File testFile = new File("EventListPA4.csv");
        if (testFile.exists()) {
            basePath = "";
        } else {
            basePath = "src/";
        }
        
        // Initialize DataStore
        ds = new DataStore();
        try {
            ds.loadEvents(basePath + "EventListPA4.csv");
            ds.loadCustomers(basePath + "CustomerListPA4.csv");
            EventManager.initializeLargestEventId(ds.getAllEvents());
            System.out.println("Loaded PA4 data files.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Setup UI
        setupUI();
    }
    
    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels
        loginPanel = createLoginPanel();
        customerPanel = createCustomerPanel();
        adminPanel = createAdminPanel();
        
        // Add panels to card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(customerPanel, "CUSTOMER");
        mainPanel.add(adminPanel, "ADMIN");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("Ticket Miner", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);
        
        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JButton customerBtn = new JButton("Customer Login");
        customerBtn.setPreferredSize(new Dimension(200, 50));
        customerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        customerBtn.addActionListener(e -> showCustomerLogin());
        
        JButton adminBtn = new JButton("Administrator");
        adminBtn.setPreferredSize(new Dimension(200, 50));
        adminBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        adminBtn.addActionListener(e -> {
            Logger.log("Administrator role selected");
            cardLayout.show(mainPanel, "ADMIN");
        });
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(200, 50));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        exitBtn.addActionListener(e -> exitApplication());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(customerBtn, gbc);
        gbc.gridy = 1;
        centerPanel.add(adminBtn, gbc);
        gbc.gridy = 2;
        centerPanel.add(exitBtn, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void showCustomerLogin() {
        JPanel loginDialog = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginDialog.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginDialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginDialog.add(passwordField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, loginDialog, 
            "Customer Login", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            Customer cust = ds.findCustomer(username);
            if (cust == null || !cust.checkPassword(password)) {
                JOptionPane.showMessageDialog(this, "Invalid login", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                Logger.log("Failed login: " + username);
                return;
            }
            
            currentCustomer = cust;
            Logger.log("Login: " + username);
            updateCustomerPanel();
            cardLayout.show(mainPanel, "CUSTOMER");
        }
    }
    
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel with customer info and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel customerInfo = new JLabel();
        customerInfo.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(customerInfo, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            Logger.log("Logout: " + currentCustomer.getUsername());
            currentCustomer = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton browseBtn = new JButton("Browse Available Events");
        browseBtn.addActionListener(e -> browseEvents());
        centerPanel.add(browseBtn);
        
        JButton purchaseBtn = new JButton("Purchase Tickets");
        purchaseBtn.addActionListener(e -> purchaseTickets());
        centerPanel.add(purchaseBtn);
        
        JButton accountBtn = new JButton("View Account Information");
        accountBtn.addActionListener(e -> viewAccountInformation());
        centerPanel.add(accountBtn);
        
        JButton historyBtn = new JButton("View Purchase History");
        historyBtn.addActionListener(e -> viewPurchaseHistory());
        centerPanel.add(historyBtn);
        
        JButton receiptBtn = new JButton("Generate Receipt Summary");
        receiptBtn.addActionListener(e -> generateReceiptSummary());
        centerPanel.add(receiptBtn);
        
        JButton invoiceBtn = new JButton("Generate Invoice Summary");
        invoiceBtn.addActionListener(e -> generateInvoiceSummary());
        centerPanel.add(invoiceBtn);
        
        JButton cancelBtn = new JButton("Cancel Ticket Purchase");
        cancelBtn.addActionListener(e -> cancelTicketPurchase());
        centerPanel.add(cancelBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Store customer info label for updates
        panel.putClientProperty("customerInfoLabel", customerInfo);
        
        return panel;
    }
    
    private void updateCustomerPanel() {
        if (currentCustomer != null) {
            JLabel customerInfo = (JLabel) ((JPanel) customerPanel.getComponent(0)).getComponent(0);
            customerInfo.setText("Customer: " + currentCustomer.getFullName() + 
                " | Balance: $" + String.format("%.2f", currentCustomer.getBalance()));
        }
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Administrator Menu", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.CENTER);
        
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            Logger.log("Admin exited to main menu");
            cardLayout.show(mainPanel, "LOGIN");
        });
        topPanel.add(backBtn, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with scrollable buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add all admin buttons
        addAdminButton(centerPanel, "A) Inquire by Event ID", e -> inquireById());
        addAdminButton(centerPanel, "B) Inquire by Event Name", e -> inquireByName());
        addAdminButton(centerPanel, "C) Summary of All Events", e -> summaryAll());
        addAdminButton(centerPanel, "D) Seats Remaining", e -> seatsRemaining());
        addAdminButton(centerPanel, "E) Revenue Details", e -> revenueDetails());
        addAdminButton(centerPanel, "F) Expected Profit", e -> profitExpected());
        addAdminButton(centerPanel, "G) Actual Profit", e -> profitActual());
        addAdminButton(centerPanel, "H) Tax Collected Per Event", e -> taxCollectedPerEvent());
        addAdminButton(centerPanel, "I) Create New Event", e -> createNewEvent());
        addAdminButton(centerPanel, "J) Process Automatic Purchases", e -> processAutoPurchases());
        addAdminButton(centerPanel, "K) Generate Customer Receipt Summary", e -> generateCustomerReceiptSummary());
        addAdminButton(centerPanel, "L) Generate Customer Invoice Summary", e -> generateCustomerInvoiceSummary());
        addAdminButton(centerPanel, "M) Generate 6 Required Receipt Summaries", e -> generateRequiredReceiptSummaries());
        addAdminButton(centerPanel, "N) View Fees for Event", e -> viewFeesForEvent());
        addAdminButton(centerPanel, "O) View Fees for All Events", e -> viewFeesForAllEvents());
        addAdminButton(centerPanel, "P) Cancel Event", e -> cancelEvent());
        addAdminButton(centerPanel, "Q) Generate Electronic Tickets (8 Customers)", e -> generateElectronicTickets());
        addAdminButton(centerPanel, "R) View Electronic Tickets", e -> viewElectronicTickets());
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addAdminButton(JPanel panel, String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        panel.add(btn);
    }
    
    // ====================================================================
    // Customer Methods
    // ====================================================================
    
    private void browseEvents() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Available Events ===\n");
        sb.append(String.format("%-5s %-12s %-40s %-12s %-10s %-20s\n", 
            "ID", "Type", "Name", "Date", "Time", "Venue"));
        sb.append("=====================================================================================================\n");
        
        for (Event event : ds.getAllEvents()) {
            String eventName = event.getName();
            if (eventName.length() > 40) {
                eventName = eventName.substring(0, 37) + "...";
            }
            
            String eventType = event instanceof Sport ? "Sport"
                             : event instanceof Concert ? "Concert"
                             : event instanceof Festival ? "Festival"
                             : "Unknown";
            
            sb.append(String.format("%-5s %-12s %-40s %-12s %-10s %-20s\n",
                event.getId(), eventType, eventName, event.getDate(), event.getTime(),
                event.getVenue().getName()));
        }
        
        Logger.log("Customer " + currentCustomer.getUsername() + " viewed events");
        showTextDialog("Available Events", sb.toString());
    }
    
    private void purchaseTickets() {
        // Category selection
        String[] categories = {"Sport", "Concert", "Festival"};
        String category = (String) JOptionPane.showInputDialog(this,
            "Select category:", "Purchase Tickets",
            JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]);
        
        if (category == null) return;
        
        // Filter events
        List<Event> filtered = new ArrayList<>();
        for (Event e : ds.getAllEvents()) {
            if (category.equals("Sport") && e instanceof Sport) filtered.add(e);
            if (category.equals("Concert") && e instanceof Concert) filtered.add(e);
            if (category.equals("Festival") && e instanceof Festival) filtered.add(e);
        }
        
        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events available in that category.");
            return;
        }
        
        // Select event
        String[] eventOptions = new String[filtered.size()];
        for (int i = 0; i < filtered.size(); i++) {
            Event e = filtered.get(i);
            eventOptions[i] = e.getId() + " - " + e.getName() + " (" + e.getDate() + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select event:", "Purchase Tickets",
            JOptionPane.QUESTION_MESSAGE, null, eventOptions, eventOptions[0]);
        
        if (selected == null) return;
        
        int eventIndex = -1;
        for (int i = 0; i < eventOptions.length; i++) {
            if (eventOptions[i].equals(selected)) {
                eventIndex = i;
                break;
            }
        }
        
        if (eventIndex == -1) return;
        Event evt = filtered.get(eventIndex);
        
        // Show event details
        StringBuilder details = new StringBuilder();
        evt.displayEventDetailsForCustomer();
        // Note: displayEventDetailsForCustomer() prints to console, we'll show in dialog
        showTextDialog("Event Details", getEventDetailsString(evt));
        
        // Select ticket type
        TicketType[] types = TicketType.values();
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeNames[i] = types[i].toString();
        }
        String typeStr = (String) JOptionPane.showInputDialog(this,
            "Select ticket type:", "Purchase Tickets",
            JOptionPane.QUESTION_MESSAGE, null, typeNames, typeNames[0]);
        
        if (typeStr == null) return;
        TicketType type = TicketType.valueOf(typeStr);
        
        // Select quantity
        String qtyStr = JOptionPane.showInputDialog(this, "Quantity (1-6):", "Purchase Tickets",
            JOptionPane.QUESTION_MESSAGE);
        if (qtyStr == null) return;
        
        int qty = 0;
        try {
            qty = Integer.parseInt(qtyStr.trim());
            if (qty < 1 || qty > 6) {
                JOptionPane.showMessageDialog(this, "Quantity must be between 1 and 6.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }
        
        // Check availability
        if (!evt.sellTickets(type, qty)) {
            JOptionPane.showMessageDialog(this, "Sorry, not enough tickets available.");
            Logger.log("Failed purchase, short on tickets: " + currentCustomer.getUsername());
            return;
        }
        
        // Create invoice
        Invoice inv = new Invoice(currentCustomer, evt, type, qty);
        double totalCost = inv.getTotalPrice();
        
        // Check funds
        if (totalCost > currentCustomer.getBalance()) {
            JOptionPane.showMessageDialog(this, "Insufficient funds.");
            Logger.log("Insufficient funds: " + currentCustomer.getUsername());
            return;
        }
        
        // Charge customer
        currentCustomer.charge(totalCost);
        currentCustomer.addInvoice(inv);
        evt.addTicketPurchase(inv);
        
        // Show confirmation
        StringBuilder confirm = new StringBuilder();
        confirm.append("Purchase Successful!\n\n");
        inv.displayInvoice(evt);
        // Note: displayInvoice prints to console, we'll show summary
        confirm.append("Invoice ID: ").append(inv.getId()).append("\n");
        confirm.append("Total: $").append(String.format("%.2f", totalCost)).append("\n");
        
        JOptionPane.showMessageDialog(this, confirm.toString(), "Purchase Confirmation",
            JOptionPane.INFORMATION_MESSAGE);
        
        Logger.log(String.format("Invoice %s for %s, event %s, type %s, qty %d, total $%.2f",
            inv.getId(), currentCustomer.getUsername(), evt.getId(), type, qty, totalCost));
        
        updateCustomerPanel();
    }
    
    private String getEventDetailsString(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event ID: ").append(event.getId()).append("\n");
        sb.append("Name: ").append(event.getName()).append("\n");
        sb.append("Date: ").append(event.getDate()).append("\n");
        sb.append("Time: ").append(event.getTime()).append("\n");
        sb.append("Venue: ").append(event.getVenue().getName()).append("\n");
        sb.append("\nTicket Prices:\n");
        sb.append("VIP: $").append(String.format("%.2f", event.getPrice(TicketType.VIP))).append("\n");
        sb.append("Gold: $").append(String.format("%.2f", event.getPrice(TicketType.GOLD))).append("\n");
        sb.append("Silver: $").append(String.format("%.2f", event.getPrice(TicketType.SILVER))).append("\n");
        sb.append("Bronze: $").append(String.format("%.2f", event.getPrice(TicketType.BRONZE))).append("\n");
        sb.append("General Admission: $").append(String.format("%.2f", event.getPrice(TicketType.GENERAL_ADMISSION))).append("\n");
        return sb.toString();
    }
    
    private void viewAccountInformation() {
        StringBuilder sb = new StringBuilder();
        currentCustomer.displayCustomerInfo();
        // Note: displayCustomerInfo prints to console, we'll show in dialog
        sb.append("Customer ID: ").append(currentCustomer.getId()).append("\n");
        sb.append("Name: ").append(currentCustomer.getFullName()).append("\n");
        sb.append("Username: ").append(currentCustomer.getUsername()).append("\n");
        sb.append("Balance: $").append(String.format("%.2f", currentCustomer.getBalance())).append("\n");
        sb.append("Membership: ").append(currentCustomer.hasTicketMinerMembership() ? "Yes" : "No").append("\n");
        
        Logger.log("Customer " + currentCustomer.getUsername() + " viewed account");
        showTextDialog("Account Information", sb.toString());
    }
    
    private void viewPurchaseHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Purchase History ===\n");
        List<Invoice> invoices = currentCustomer.getInvoices();
        
        if (invoices.isEmpty()) {
            sb.append("No purchase history found.");
        } else {
            sb.append(String.format("%-10s %-40s %-15s %-5s %-10s\n",
                "Invoice", "Event", "Ticket Type", "Qty", "Total"));
            sb.append("=======================================================================\n");
            
            for (Invoice invoice : invoices) {
                String eventName = invoice.getEventName();
                if (eventName.length() > 40) {
                    eventName = eventName.substring(0, 37) + "...";
                }
                
                String total = "$" + String.format("%.2f", invoice.getTotalPrice());
                
                sb.append(String.format("%-10s %-40s %-15s %-5d %-10s\n",
                    invoice.getId(), eventName, invoice.getTicketType(),
                    invoice.getQuantity(), total));
            }
        }
        
        Logger.log("Customer " + currentCustomer.getUsername() + " viewed purchase history");
        showTextDialog("Purchase History", sb.toString());
    }
    
    private void generateReceiptSummary() {
        String[] options = {"All purchases", "Specific invoice"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Generate receipt for:", "Receipt Summary",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (choice == null) return;
        
        try {
            if (choice.equals("All purchases")) {
                String filename = currentCustomer.getUsername() + "_ReceiptSummary.txt";
                ReceiptGenerator.generateCustomerReceiptSummary(currentCustomer, filename);
                JOptionPane.showMessageDialog(this, "Receipt summary generated: " + filename);
                Logger.log("Customer " + currentCustomer.getUsername() + " generated receipt summary: " + filename);
            } else {
                List<Invoice> invoices = currentCustomer.getInvoices();
                if (invoices.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No invoices found.");
                    return;
                }
                
                String[] invoiceOptions = new String[invoices.size()];
                for (int i = 0; i < invoices.size(); i++) {
                    Invoice inv = invoices.get(i);
                    invoiceOptions[i] = inv.getId() + " - " + inv.getEventName() + " ($" +
                        String.format("%.2f", inv.getTotalPrice()) + ")";
                }
                
                String selected = (String) JOptionPane.showInputDialog(this,
                    "Select invoice:", "Receipt Summary",
                    JOptionPane.QUESTION_MESSAGE, null, invoiceOptions, invoiceOptions[0]);
                
                if (selected == null) return;
                
                int invIndex = -1;
                for (int i = 0; i < invoiceOptions.length; i++) {
                    if (invoiceOptions[i].equals(selected)) {
                        invIndex = i;
                        break;
                    }
                }
                
                if (invIndex == -1) return;
                Invoice selectedInvoice = invoices.get(invIndex);
                String filename = currentCustomer.getUsername() + "_Invoice" + 
                    selectedInvoice.getId() + "_Receipt.txt";
                ReceiptGenerator.generateReceiptSummary(selectedInvoice, filename);
                JOptionPane.showMessageDialog(this, "Receipt generated: " + filename);
                Logger.log("Customer " + currentCustomer.getUsername() + 
                    " generated receipt for invoice " + selectedInvoice.getId());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating receipt: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating receipt for " + currentCustomer.getUsername() + ": " + e.getMessage());
        }
    }
    
    private void generateInvoiceSummary() {
        String filename = currentCustomer.getUsername() + "_InvoiceSummary.txt";
        try {
            InvoiceSummaryGenerator.generateInvoiceSummary(currentCustomer, filename);
            JOptionPane.showMessageDialog(this, "Invoice summary generated: " + filename);
            Logger.log("Customer " + currentCustomer.getUsername() + " generated invoice summary: " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating invoice summary: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating invoice summary for " + currentCustomer.getUsername() + ": " + e.getMessage());
        }
    }
    
    private void cancelTicketPurchase() {
        List<Invoice> invoices = currentCustomer.getInvoices();
        
        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No purchases to cancel.");
            return;
        }
        
        String[] invoiceOptions = new String[invoices.size()];
        for (int i = 0; i < invoices.size(); i++) {
            Invoice inv = invoices.get(i);
            String eventName = inv.getEventName();
            if (eventName.length() > 40) {
                eventName = eventName.substring(0, 37) + "...";
            }
            invoiceOptions[i] = (i + 1) + ". " + inv.getId() + " - " + eventName + 
                " (" + inv.getTicketType() + ", Qty: " + inv.getQuantity() + ", $" +
                String.format("%.2f", inv.getTotalPrice()) + ")";
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select purchase to cancel:", "Cancel Purchase",
            JOptionPane.QUESTION_MESSAGE, null, invoiceOptions, invoiceOptions[0]);
        
        if (selected == null) return;
        
        int choice = -1;
        for (int i = 0; i < invoiceOptions.length; i++) {
            if (invoiceOptions[i].equals(selected)) {
                choice = i;
                break;
            }
        }
        
        if (choice < 0 || choice >= invoices.size()) return;
        
        Invoice selectedInvoice = invoices.get(choice);
        Event event = selectedInvoice.getEvent();
        
        if (event == null) {
            JOptionPane.showMessageDialog(this, "Cannot cancel: Event information not available.");
            return;
        }
        
        double refundAmount = selectedInvoice.getTicketCosts();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel this purchase?\n\nInvoice ID: " + selectedInvoice.getId() + "\n" +
            "Event: " + selectedInvoice.getEventName() + "\n" +
            "Refund Amount: $" + String.format("%.2f", refundAmount),
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Refund customer
        currentCustomer.charge(-refundAmount);
        currentCustomer.removeInvoice(selectedInvoice);
        
        JOptionPane.showMessageDialog(this,
            "Purchase cancelled successfully!\n" +
            "Refunded: $" + String.format("%.2f", refundAmount) + "\n" +
            "New Balance: $" + String.format("%.2f", currentCustomer.getBalance()));
        
        Logger.log(String.format("Customer %s cancelled invoice %s, refunded $%.2f",
            currentCustomer.getUsername(), selectedInvoice.getId(), refundAmount));
        
        updateCustomerPanel();
    }
    
    // ====================================================================
    // Admin Methods
    // ====================================================================
    
    private void inquireById() {
        String eventId = JOptionPane.showInputDialog(this, "Enter Event ID:", "Inquire by ID",
            JOptionPane.QUESTION_MESSAGE);
        
        if (eventId == null || eventId.trim().isEmpty()) return;
        
        Event e = ds.findEvent(eventId.trim());
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Event not found.");
            return;
        }
        
        Logger.log("Admin lookup ID=" + e.getId());
        showTextDialog("Event Details", getEventDetailsString(e));
    }
    
    private void inquireByName() {
        String name = JOptionPane.showInputDialog(this, "Enter Event Name (or partial name):",
            "Inquire by Name", JOptionPane.QUESTION_MESSAGE);
        
        if (name == null || name.trim().isEmpty()) return;
        
        String searchName = name.trim().toLowerCase();
        List<Event> matches = new ArrayList<>();
        for (Event e : ds.getAllEvents()) {
            if (e.getName().toLowerCase().contains(searchName)) {
                matches.add(e);
            }
        }
        
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events found matching: " + name);
            Logger.log("Admin lookup Name contains '" + name + "' - no results");
            return;
        }
        
        if (matches.size() == 1) {
            Event e = matches.get(0);
            Logger.log("Admin lookup Name contains '" + name + "' - ID=" + e.getId());
            showTextDialog("Event Details", getEventDetailsString(e));
            return;
        }
        
        // Multiple matches - show selection
        String[] options = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            Event e = matches.get(i);
            options[i] = e.getId() + " - " + e.getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Found " + matches.size() + " matching events. Select one:",
            "Select Event", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selected == null) return;
        
        int index = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selected)) {
                index = i;
                break;
            }
        }
        
        if (index >= 0) {
            Event selectedEvent = matches.get(index);
            Logger.log("Admin lookup Name contains '" + name + "' - selected ID=" + selectedEvent.getId());
            showTextDialog("Event Details", getEventDetailsString(selectedEvent));
        }
    }
    
    private void summaryAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Summary of All Events ===\n\n");
        for (Event e : ds.getAllEvents()) {
            sb.append(String.format("ID:%s | %-20s | Remain:%d | Rev:%.2f\n",
                e.getId(), e.getName(), e.totalRemaining(), e.totalRevenue()));
        }
        Logger.log("Admin summary all events");
        showTextDialog("Summary of All Events", sb.toString());
    }
    
    private void seatsRemaining() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Seats Remaining Report ===\n\n");
        for (Event e : ds.getAllEvents()) {
            sb.append(String.format("Event ID: %s - %s\n", e.getId(), e.getName()));
            sb.append(String.format("  Total Remaining: %d\n", e.totalRemaining()));
            sb.append(String.format("  VIP Remaining: %d\n", e.remaining(TicketType.VIP)));
            sb.append(String.format("  Gold Remaining: %d\n", e.remaining(TicketType.GOLD)));
            sb.append(String.format("  Silver Remaining: %d\n", e.remaining(TicketType.SILVER)));
            sb.append(String.format("  Bronze Remaining: %d\n", e.remaining(TicketType.BRONZE)));
            sb.append(String.format("  General Admission Remaining: %d\n\n",
                e.remaining(TicketType.GENERAL_ADMISSION)));
        }
        Logger.log("Admin seats remaining");
        showTextDialog("Seats Remaining", sb.toString());
    }
    
    private void revenueDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Revenue Details Report ===\n\n");
        for (Event e : ds.getAllEvents()) {
            sb.append(String.format("Event ID: %s - %s\n", e.getId(), e.getName()));
            sb.append(String.format("  VIP Revenue: $%.2f\n", e.revenue(TicketType.VIP)));
            sb.append(String.format("  Gold Revenue: $%.2f\n", e.revenue(TicketType.GOLD)));
            sb.append(String.format("  Silver Revenue: $%.2f\n", e.revenue(TicketType.SILVER)));
            sb.append(String.format("  Bronze Revenue: $%.2f\n", e.revenue(TicketType.BRONZE)));
            sb.append(String.format("  General Admission Revenue: $%.2f\n",
                e.revenue(TicketType.GENERAL_ADMISSION)));
            sb.append(String.format("  Total Revenue: $%.2f\n\n", e.totalRevenue()));
        }
        Logger.log("Admin revenue details");
        showTextDialog("Revenue Details", sb.toString());
    }
    
    private void profitExpected() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Expected Profit Report ===\n\n");
        for (Event e : ds.getAllEvents()) {
            sb.append(String.format("ID:%s exp profit:%.2f\n",
                e.getId(), e.expectedProfit()));
        }
        Logger.log("Admin expected profit");
        showTextDialog("Expected Profit", sb.toString());
    }
    
    private void profitActual() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Actual Profit Report ===\n\n");
        for (Event e : ds.getAllEvents()) {
            sb.append(String.format("ID:%s act profit:%.2f\n",
                e.getId(), e.actualProfit()));
        }
        Logger.log("Admin actual profit");
        showTextDialog("Actual Profit", sb.toString());
    }
    
    private void taxCollectedPerEvent() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Tax Collected Per Event Report ===\n\n");
        double totalTax = 0.0;
        
        for (Event e : ds.getAllEvents()) {
            double eventTax = e.getTotalTaxCollected();
            totalTax += eventTax;
            sb.append(String.format("Event ID: %s - %s\n", e.getId(), e.getName()));
            sb.append(String.format("  Total Tax Collected: $%.2f\n\n", eventTax));
        }
        
        sb.append(String.format("Total Tax Collected (All Events): $%.2f\n", totalTax));
        Logger.log("Admin viewed tax collected per event");
        showTextDialog("Tax Collected Per Event", sb.toString());
    }
    
    private void createNewEvent() {
        // Use a dialog-based approach for event creation
        // For simplicity, we'll use a series of input dialogs
        // In a production system, you'd want a proper form dialog
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField nameField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        JTextField timeField = new JTextField(20);
        JTextField gaPriceField = new JTextField(20);
        
        String[] venueOptions = {"Sun Bowl Stadium", "Don Haskins Center", "Magoffin Auditorium",
            "San Jacinto Plaza", "Centennial Plaza"};
        JComboBox<String> venueCombo = new JComboBox<>(venueOptions);
        
        String[] typeOptions = {"Sport", "Concert", "Festival"};
        JComboBox<String> typeCombo = new JComboBox<>(typeOptions);
        
        int row = 0;
        formPanel.add(new JLabel("Event Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Date (MM/DD/YYYY):"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Time (HH:MM AM/PM):"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1;
        formPanel.add(venueCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Event Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(new JLabel("General Admission Price:"), gbc);
        gbc.gridx = 1;
        formPanel.add(gaPriceField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, formPanel, "Create New Event",
            JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            // Create event using EventManager
            // We need to simulate Scanner input for EventManager.createEventFromConsole
            // For now, we'll create a simplified version
            try {
                java.util.Scanner scanner = new java.util.Scanner(
                    nameField.getText() + "\n" +
                    dateField.getText() + "\n" +
                    timeField.getText() + "\n" +
                    (venueCombo.getSelectedIndex() + 1) + "\n" +
                    gaPriceField.getText() + "\n" +
                    (typeCombo.getSelectedIndex() + 1) + "\n" +
                    "no\n"
                );
                
                Event newEvent = EventManager.createEventFromConsole(scanner, ds.getVenues());
                if (newEvent != null) {
                    ds.addEvent(newEvent);
                    JOptionPane.showMessageDialog(this, "Event created successfully!\nEvent ID: " + newEvent.getId());
                    Logger.log("Admin created new event: " + newEvent.getId() + " - " + newEvent.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Event creation failed.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating event: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void processAutoPurchases() {
        String[] fileOptions = {"AutoPurchas10k.csv", "AutoPurchas100k.csv", "AutoPurchas500k.csv", "Custom filename"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Select autopurchase file:", "Process Automatic Purchases",
            JOptionPane.QUESTION_MESSAGE, null, fileOptions, fileOptions[0]);
        
        if (choice == null) return;
        
        String filename;
        if (choice.equals("Custom filename")) {
            filename = JOptionPane.showInputDialog(this, "Enter filename:",
                "Process Automatic Purchases", JOptionPane.QUESTION_MESSAGE);
            if (filename == null) return;
        } else {
            filename = basePath + choice;
        }
        
        Logger.log("Admin processing autopurchases from: " + filename);
        AutoPurchase.processAutoPurchases(filename, ds);
        JOptionPane.showMessageDialog(this, "Automatic purchases processed. Check console for details.");
    }
    
    private void generateCustomerReceiptSummary() {
        String username = JOptionPane.showInputDialog(this, "Enter customer username:",
            "Generate Receipt Summary", JOptionPane.QUESTION_MESSAGE);
        
        if (username == null || username.trim().isEmpty()) return;
        
        Customer customer = ds.findCustomer(username.trim());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found: " + username);
            return;
        }
        
        String filename = JOptionPane.showInputDialog(this,
            "Enter output filename (without extension, or press Cancel for default):",
            "Generate Receipt Summary", JOptionPane.QUESTION_MESSAGE);
        
        if (filename == null || filename.trim().isEmpty()) {
            filename = username + "_ReceiptSummary";
        }
        
        filename = filename.trim() + ".txt";
        
        try {
            ReceiptGenerator.generateCustomerReceiptSummary(customer, filename);
            JOptionPane.showMessageDialog(this, "Receipt summary generated: " + filename);
            Logger.log("Admin generated receipt summary for " + username + ": " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating receipt: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating receipt for " + username + ": " + e.getMessage());
        }
    }
    
    private void generateCustomerInvoiceSummary() {
        String username = JOptionPane.showInputDialog(this, "Enter customer username:",
            "Generate Invoice Summary", JOptionPane.QUESTION_MESSAGE);
        
        if (username == null || username.trim().isEmpty()) return;
        
        Customer customer = ds.findCustomer(username.trim());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found: " + username);
            return;
        }
        
        String filename = JOptionPane.showInputDialog(this,
            "Enter output filename (without extension, or press Cancel for default):",
            "Generate Invoice Summary", JOptionPane.QUESTION_MESSAGE);
        
        if (filename == null || filename.trim().isEmpty()) {
            filename = username + "_InvoiceSummary";
        }
        
        filename = filename.trim() + ".txt";
        
        try {
            InvoiceSummaryGenerator.generateInvoiceSummary(customer, filename);
            JOptionPane.showMessageDialog(this, "Invoice summary generated: " + filename);
            Logger.log("Admin generated invoice summary for " + username + ": " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating invoice summary: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating invoice summary for " + username + ": " + e.getMessage());
        }
    }
    
    private void generateRequiredReceiptSummaries() {
        try {
            GenerateReceiptSummaries.generateRequiredReceipts(ds);
            JOptionPane.showMessageDialog(this, "6 required receipt summaries generated successfully.");
            Logger.log("Admin generated 6 required receipt summaries");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating required receipts: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating required receipts: " + e.getMessage());
        }
    }
    
    private void viewFeesForEvent() {
        // Show available events with fees
        List<Event> eventsWithFees = new ArrayList<>();
        for (Event event : ds.getAllEvents()) {
            if (event.getTotalFeesCollected() > 0) {
                eventsWithFees.add(event);
            }
        }
        
        if (eventsWithFees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events have collected fees yet.");
            return;
        }
        
        String[] eventOptions = new String[eventsWithFees.size()];
        for (int i = 0; i < eventsWithFees.size(); i++) {
            eventOptions[i] = eventsWithFees.get(i).getId() + " - " + eventsWithFees.get(i).getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select event to view fees:", "View Fees for Event",
            JOptionPane.QUESTION_MESSAGE, null, eventOptions, eventOptions[0]);
        
        if (selected == null) return;
        
        int index = -1;
        for (int i = 0; i < eventOptions.length; i++) {
            if (eventOptions[i].equals(selected)) {
                index = i;
                break;
            }
        }
        
        if (index < 0) return;
        
        Event event = eventsWithFees.get(index);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Fees Collected for Event ===\n\n");
        sb.append("Event ID: ").append(event.getId()).append("\n");
        sb.append("Event Name: ").append(event.getName()).append("\n\n");
        sb.append("--- TICKETMINER COMPANY FEES ---\n");
        sb.append(String.format("Convenience Fees: $%.2f\n", event.getTotalConvenienceFees()));
        sb.append(String.format("Service Fees: $%.2f\n", event.getTotalServiceFees()));
        sb.append(String.format("Charity Fees: $%.2f\n", event.getTotalCharityFees()));
        sb.append(String.format("Total Fees: $%.2f\n", event.getTotalFeesCollected()));
        sb.append("==================================");
        
        Logger.log("Admin viewed fees for event " + event.getId());
        showTextDialog("Fees for Event", sb.toString());
    }
    
    private void viewFeesForAllEvents() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Fees Collected for All Events ===\n\n");
        
        double totalConvenience = 0.0;
        double totalService = 0.0;
        double totalCharity = 0.0;
        double grandTotal = 0.0;
        
        for (Event event : ds.getAllEvents()) {
            double convenience = event.getTotalConvenienceFees();
            double service = event.getTotalServiceFees();
            double charity = event.getTotalCharityFees();
            double total = event.getTotalFeesCollected();
            
            totalConvenience += convenience;
            totalService += service;
            totalCharity += charity;
            grandTotal += total;
            
            sb.append(String.format("Event ID: %s - %s\n", event.getId(), event.getName()));
            sb.append(String.format("  Convenience Fees: $%.2f\n", convenience));
            sb.append(String.format("  Service Fees: $%.2f\n", service));
            sb.append(String.format("  Charity Fees: $%.2f\n", charity));
            sb.append(String.format("  Total Fees: $%.2f\n\n", total));
        }
        
        sb.append("--- GRAND TOTALS ---\n");
        sb.append(String.format("Total Convenience Fees: $%.2f\n", totalConvenience));
        sb.append(String.format("Total Service Fees: $%.2f\n", totalService));
        sb.append(String.format("Total Charity Fees: $%.2f\n", totalCharity));
        sb.append(String.format("Total Fees (All Events): $%.2f\n", grandTotal));
        sb.append("=====================");
        
        Logger.log("Admin viewed fees for all events");
        showTextDialog("Fees for All Events", sb.toString());
    }
    
    private void cancelEvent() {
        String eventId = JOptionPane.showInputDialog(this, "Enter Event ID to cancel:",
            "Cancel Event", JOptionPane.QUESTION_MESSAGE);
        
        if (eventId == null || eventId.trim().isEmpty()) return;
        
        Event event = ds.findEvent(eventId.trim());
        if (event == null) {
            JOptionPane.showMessageDialog(this, "Event not found.");
            return;
        }
        
        Invoice[] purchases = event.getTicketsPurchased();
        int purchaseCount = event.getTicketCount();
        double totalRefund = 0.0;
        for (int i = 0; i < purchaseCount; i++) {
            totalRefund += purchases[i].getTotalPrice();
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "WARNING: This will cancel the event and refund all customers.\n\n" +
            "Event ID: " + event.getId() + "\n" +
            "Event Name: " + event.getName() + "\n" +
            "Total Purchases: " + purchaseCount + "\n" +
            "Total Refund Amount: $" + String.format("%.2f", totalRefund) + "\n\n" +
            "Are you sure you want to cancel this event?",
            "Confirm Event Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Refund all customers
        int refundedCount = 0;
        double totalRefunded = 0.0;
        double totalTicketCosts = 0.0;
        double totalFeesRefunded = 0.0;
        
        for (int i = 0; i < purchaseCount; i++) {
            Invoice invoice = purchases[i];
            Customer customer = invoice.getCustomer();
            
            if (customer != null) {
                double refundAmount = invoice.getTotalPrice();
                customer.charge(-refundAmount);
                totalRefunded += refundAmount;
                totalTicketCosts += invoice.getTicketCosts();
                totalFeesRefunded += invoice.getTotalFees();
                refundedCount++;
                
                customer.removeInvoice(invoice);
                event.removeTicketPurchaseFees(invoice);
            }
        }
        
        event.resetAllFees();
        
        JOptionPane.showMessageDialog(this,
            "Event Cancelled\n\n" +
            "Customers refunded: " + refundedCount + "\n" +
            "Total Ticket Costs Refunded: $" + String.format("%.2f", totalTicketCosts) + "\n" +
            "Total Fees Refunded: $" + String.format("%.2f", totalFeesRefunded) + "\n" +
            "Total Amount Refunded: $" + String.format("%.2f", totalRefunded));
        
        Logger.log(String.format("Admin cancelled event %s, refunded %d customers, total $%.2f",
            eventId, refundedCount, totalRefunded));
    }
    
    private void generateElectronicTickets() {
        try {
            ElectronicTicketGenerator.generateTicketsFor8Customers(ds);
            JOptionPane.showMessageDialog(this, 
                "Electronic tickets generated successfully!\n" +
                "Check console for details.\n" +
                "Ticket files saved in current directory.",
                "Tickets Generated", JOptionPane.INFORMATION_MESSAGE);
            Logger.log("Admin generated electronic tickets for 8 customers");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating tickets: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.log("Error generating electronic tickets: " + e.getMessage());
        }
    }
    
    private void viewElectronicTickets() {
        // Find all ticket files
        java.io.File currentDir = new java.io.File(".");
        java.io.File[] files = currentDir.listFiles();
        
        List<java.io.File> ticketFiles = new ArrayList<>();
        if (files != null) {
            for (java.io.File file : files) {
                if (file.isFile() && file.getName().contains("_Ticket_TKT-") && file.getName().endsWith(".txt")) {
                    ticketFiles.add(file);
                }
            }
        }
        
        // Also check src directory
        java.io.File srcDir = new java.io.File("src");
        if (srcDir.exists() && srcDir.isDirectory()) {
            java.io.File[] srcFiles = srcDir.listFiles();
            if (srcFiles != null) {
                for (java.io.File file : srcFiles) {
                    if (file.isFile() && file.getName().contains("_Ticket_TKT-") && file.getName().endsWith(".txt")) {
                        ticketFiles.add(file);
                    }
                }
            }
        }
        
        if (ticketFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No ticket files found.\nPlease generate tickets first using option Q.",
                "No Tickets Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (ticketFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No ticket files found.\nPlease generate tickets first using option Q.",
                "No Tickets Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Sort by name
        ticketFiles.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
        
        // Create list of ticket file names
        String[] ticketOptions = new String[ticketFiles.size()];
        for (int i = 0; i < ticketFiles.size(); i++) {
            ticketOptions[i] = ticketFiles.get(i).getName();
        }
        
        // Let user select a ticket to view
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select a ticket to view:", "View Electronic Tickets",
            JOptionPane.QUESTION_MESSAGE, null, ticketOptions, ticketOptions[0]);
        
        if (selected == null) return;
        
        // Find and read the selected file
        java.io.File selectedFile = null;
        for (java.io.File file : ticketFiles) {
            if (file.getName().equals(selected)) {
                selectedFile = file;
                break;
            }
        }
        
        if (selectedFile == null || !selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, "Ticket file not found: " + selected,
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Read and display the ticket
        try {
            StringBuilder ticketContent = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(selectedFile));
            String line;
            while ((line = reader.readLine()) != null) {
                ticketContent.append(line).append("\n");
            }
            reader.close();
            
            showTextDialog("Electronic Ticket: " + selected, ticketContent.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading ticket file: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ====================================================================
    // Helper Methods
    // ====================================================================
    
    private void showTextDialog(String title, String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?\nAll data will be saved.",
            "Exit", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Logger.log("Program terminated");
                String outputBasePath = new File("EventListPA4.csv").exists() ? "" : "src/";
                ds.writeUpdatedEvents(outputBasePath + "EventList" + dataVersion + "_UPDATED.csv");
                ds.writeUpdatedCustomers(outputBasePath + "CustomerList" + dataVersion + "_UPDATED.csv");
                Logger.flushToFile("ticketminer.log");
                System.exit(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new TicketMinerGUI().setVisible(true);
        });
    }
}

