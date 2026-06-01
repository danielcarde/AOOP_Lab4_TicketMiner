import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DataStore class for managing events, customers, and venues.
 * 
 * Taken from Miranda Macias
 * This class provides centralized data management with:
 * - Loading events and customers from CSV files
 * - Writing updated data back to CSV files
 * - Finding events and customers by ID or username
 * - Efficient storage using Maps
 */
public class DataStore {
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Event> events = new LinkedHashMap<>();
    private final Map<String, Venue> venues = new HashMap<>();

    /**
     * Loads customers from a CSV file.
     * Taken from Miranda Macias.
     * Enhanced to handle variable column order - PA3 Requirement 4.
     * 
     * @param fn Filename of the CSV file to load
     * @throws IOException if file cannot be read
     */
    public void loadCustomers(String fn) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fn))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return; // Empty file
            }
            
            // Remove BOM (Byte Order Mark) if present
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            
            // PA3: Handle variable column order by reading header
            // Use parseCSVLine to handle the header the same way as data lines
            String[] header = parseCSVLine(headerLine);
            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                columnMap.put(header[i].trim(), i);
            }
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = parseCSVLine(line);
                if (f.length < 5) continue; // Skip invalid lines
                
                // Get column indices with fallback - use -1 to indicate not found
                int lastIdx = columnMap.getOrDefault("Last Name", columnMap.getOrDefault("Last", -1));
                int firstIdx = columnMap.getOrDefault("First Name", columnMap.getOrDefault("First", -1));
                int userIdx = columnMap.getOrDefault("Username", columnMap.getOrDefault("UserName", -1));
                int passIdx = columnMap.getOrDefault("Password", -1);
                int moneyIdx = columnMap.getOrDefault("Money Available", columnMap.getOrDefault("Money", -1));
                int idIdx = columnMap.getOrDefault("ID", -1);
                int memberIdx = columnMap.getOrDefault("TicketMiner Membership", columnMap.getOrDefault("Membership", -1));
                
                // Validate required columns are found
                if (userIdx < 0 || passIdx < 0 || moneyIdx < 0 || idIdx < 0 || 
                    userIdx >= f.length || passIdx >= f.length || moneyIdx >= f.length || idIdx >= f.length) {
                    continue; // Skip line if required columns are missing
                }
                
                String user = safeGet(f, userIdx, "");
                String pass = safeGet(f, passIdx, "");
                String lastName = safeGet(f, lastIdx >= 0 ? lastIdx : 0, "");
                String firstName = safeGet(f, firstIdx >= 0 ? firstIdx : 0, "");
                String name = firstName + " " + lastName;
                
                if (user.isEmpty() || pass.isEmpty()) continue;
                
                double bal = parseD(safeGet(f, moneyIdx, "0"));
                boolean membership = false;
                if (memberIdx >= 0 && memberIdx < f.length) {
                    String memberStr = safeGet(f, memberIdx, "false");
                    membership = memberStr.equalsIgnoreCase("true") || memberStr.equalsIgnoreCase("yes");
                }
                
                int id = parseInt(safeGet(f, idIdx, "0"));
                
                // Use the enhanced constructor
                Customer cust = new Customer(id, firstName, lastName, bal, membership, user, pass);
                customers.put(user, cust);
            }
        }
    }
    
    /**
     * Parses a CSV line, handling commas within fields.
     * Simple parser that splits on commas.
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        // Add the last field
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }

    /**
     * Loads events from a CSV file.
     * Taken from Miranda Macias.
     * Enhanced to support venue cost from Brian Casio.
     * Enhanced to handle variable column order - PA3 Requirement 4.
     * 
     * @param fn Filename of the CSV file to load
     * @throws IOException if file cannot be read
     */
    public void loadEvents(String fn) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        try (BufferedReader br = new BufferedReader(new FileReader(fn))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return; // Empty file
            }
            
            // Remove BOM (Byte Order Mark) if present
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            
            // PA3 Requirement 4: Handle variable column order by reading header
            // Use parseCSVLine to handle the header the same way as data lines
            String[] header = parseCSVLine(headerLine);
            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                columnMap.put(header[i].trim(), i);
            }
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = parseCSVLine(line);
                if (f.length < 10) continue; // Skip invalid lines
                
                // Get column indices (with fallback for missing columns)
                int idIdx = columnMap.getOrDefault("Event ID", columnMap.getOrDefault("EventID", 0));
                int typeIdx = columnMap.getOrDefault("Event Type", columnMap.getOrDefault("EventType", 1));
                int nameIdx = columnMap.getOrDefault("Name", 2);
                int dateIdx = columnMap.getOrDefault("Date", columnMap.getOrDefault("Date", 3));
                int timeIdx = columnMap.getOrDefault("Time", columnMap.getOrDefault("Time", 4));
                int vipIdx = columnMap.getOrDefault("VIP Price", columnMap.getOrDefault("VIPPrice", 5));
                int goldIdx = columnMap.getOrDefault("Gold Price", columnMap.getOrDefault("GoldPrice", 6));
                int silverIdx = columnMap.getOrDefault("Silver Price", columnMap.getOrDefault("SilverPrice", 7));
                int bronzeIdx = columnMap.getOrDefault("Bronze Price", columnMap.getOrDefault("BronzePrice", 8));
                int genIdx = columnMap.getOrDefault("General Admission Price", 
                    columnMap.getOrDefault("General Admission Price", columnMap.getOrDefault("GeneralAdmissionPrice", 9)));
                int vnameIdx = columnMap.getOrDefault("Venue Name", columnMap.getOrDefault("VenueName", 10));
                int vtypeIdx = columnMap.getOrDefault("Venue Type", columnMap.getOrDefault("VenueType", 12));
                int capIdx = columnMap.getOrDefault("Capacity", 13);
                int costIdx = columnMap.getOrDefault("Cost", columnMap.getOrDefault("Cost", 14));
                
                // Extract values safely
                if (idIdx >= f.length || typeIdx >= f.length || nameIdx >= f.length) continue;
                
                String id = safeGet(f, idIdx, "").trim();
                // Normalize ID: remove leading zeros if it's a number
                if (id.matches("\\d+")) {
                    id = String.valueOf(Integer.parseInt(id));
                }
                String type = safeGet(f, typeIdx, "");
                String name = safeGet(f, nameIdx, "");
                String dateStr = safeGet(f, dateIdx, "");
                String timeStr = safeGet(f, timeIdx, "");
                
                if (id.isEmpty() || type.isEmpty() || name.isEmpty()) continue;
                
                double vip = parseD(safeGet(f, vipIdx, "0"));
                double gold = parseD(safeGet(f, goldIdx, "0"));
                double silver = parseD(safeGet(f, silverIdx, "0"));
                double bronze = parseD(safeGet(f, bronzeIdx, "0"));
                double gen = parseD(safeGet(f, genIdx, "0"));
                String vname = safeGet(f, vnameIdx, "");
                String vtype = safeGet(f, vtypeIdx, "");
                int cap = parseInt(safeGet(f, capIdx, "0"));
                double cost = parseD(safeGet(f, costIdx, "0"));
                
                if (vname.isEmpty() || cap <= 0) continue;

                // Parse date and time
                String dtStr = dateStr + " " + timeStr;
                LocalDateTime dt;
                try {
                    dt = LocalDateTime.parse(dtStr, dtf);
                } catch (Exception e) {
                    // Try alternative format
                    try {
                        DateTimeFormatter altFmt = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
                        dt = LocalDateTime.parse(dtStr, altFmt);
                    } catch (Exception e2) {
                        continue; // Skip if can't parse date
                    }
                }

                // Create or get venue (enhanced with cost)
                Venue v = venues.computeIfAbsent(vname, vn -> {
                    switch (vtype.toLowerCase()) {
                        case "stadium":
                            return new Stadium(vn, vn, "", cap, cost);
                        case "arena":
                            return new Arena(vn, vn, "", cap, cost);
                        case "auditorium":
                            return new Auditorium(vn, vn, "", cap, cost);
                        default:
                            return new OpenAir(vn, vn, "", cap, cost);
                    }
                });

                // Create price map
                Map<TicketType, Double> prices = Map.of(
                    TicketType.VIP, vip,
                    TicketType.GOLD, gold,
                    TicketType.SILVER, silver,
                    TicketType.BRONZE, bronze,
                    TicketType.GENERAL_ADMISSION, gen
                );

                // Create event using factory
                Event event = EventFactory.create(
                    type, id, name, dt, v, prices,
                    "", // sportType
                    List.of(), // teams
                    dt.toLocalDate(), // startDate
                    dt.toLocalDate(), // endDate
                    List.of() // lineup
                );
                
                // Check for fireworks in CSV
                int fireworksIdx = columnMap.getOrDefault("Fireworks Planned", -1);
                int fireworksCostIdx = columnMap.getOrDefault("Fireworks Cost", -1);
                if (fireworksIdx >= 0 && fireworksIdx < f.length) {
                    String fireworksStr = safeGet(f, fireworksIdx, "No").trim();
                    boolean hasFireworks = fireworksStr.equalsIgnoreCase("Yes") || fireworksStr.equalsIgnoreCase("True");
                    double fireworksCost = 0.0;
                    if (hasFireworks && fireworksCostIdx >= 0 && fireworksCostIdx < f.length) {
                        fireworksCost = parseD(safeGet(f, fireworksCostIdx, "0"));
                    }
                    event.setFireworks(hasFireworks, fireworksCost);
                }
                
                events.put(id, event);
            }
        }
    }
    
    /**
     * Safely gets a value from an array at the given index.
     * PA3 Requirement 4 - Helper for variable column order.
     */
    private static String safeGet(String[] array, int index, String defaultValue) {
        if (index >= 0 && index < array.length) {
            String value = array[index].trim();
            return value.isEmpty() ? defaultValue : value;
        }
        return defaultValue;
    }
    
    /**
     * Helper method to parse integer values, handling empty strings.
     * PA3 Requirement 4.
     */
    private static int parseInt(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Helper method to parse double values, handling empty strings.
     * Taken from Miranda Macias.
     */
    private static double parseD(String s) {
        return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s);
    }

    /**
     * Finds a customer by username.
     * Taken from Miranda Macias.
     * 
     * @param u Username to search for
     * @return Customer object or null if not found
     */
    public Customer findCustomer(String u) {
        return customers.get(u);
    }

    /**
     * Finds an event by ID.
     * Taken from Miranda Macias.
     * 
     * @param id Event ID to search for
     * @return Event object or null if not found
     */
    public Event findEvent(String id) {
        return events.get(id);
    }

    /**
     * Gets all events.
     * Taken from Miranda Macias.
     * 
     * @return Collection of all events
     */
    public Collection<Event> getAllEvents() {
        return events.values();
    }

    /**
     * Adds a new event to the data store.
     * PA3 Requirement 7 - Event Manager functionality.
     * 
     * @param event The event to add
     */
    public void addEvent(Event event) {
        if (event != null && event.getId() != null) {
            events.put(event.getId(), event);
        }
    }

    /**
     * Gets all venues.
     * PA3 Requirement 7 - Event Manager functionality.
     * 
     * @return Map of venue names to Venue objects
     */
    public Map<String, Venue> getVenues() {
        return venues;
    }

    /**
     * Gets all customers.
     * Used for searching customers by name in AutoPurchase.
     * 
     * @return Collection of all customers
     */
    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    /**
     * Adds a new customer to the data store.
     * Used by AutoPurchase to create customers that don't exist.
     * 
     * @param customer The customer to add
     */
    public void addCustomer(Customer customer) {
        if (customer != null && customer.getUsername() != null) {
            customers.put(customer.getUsername(), customer);
        }
    }

    /**
     * Writes updated events to a CSV file.
     * Taken from Miranda Macias.
     * Enhanced to include venue cost and additional fields.
     * 
     * @param fn Filename to write to
     * @throws IOException if file cannot be written
     */
    public void writeUpdatedEvents(String fn) throws IOException {
        try (PrintWriter w = new PrintWriter(fn)) {
            // Write header with all original columns plus new ones
            w.println("Event ID,Event Type,Name,Date,Time,VIP Price,Gold Price," +
                    "Silver Price,Bronze Price,General Admission Price,Venue Name," +
                    "Pct Seats Unavailable,Venue Type,Capacity,Cost,VIP Pct,Gold Pct," +
                    "Silver Pct,Bronze Pct,General Admission Pct,Reserved Extra Pct," +
                    "Fireworks Planned,Fireworks Cost," +
                    "VIP Seats Sold,Gold Seats Sold,Silver Seats Sold,Bronze Seats Sold," +
                    "General Admission Seats Sold," +
                    "Total VIP Revenue,Total Gold Revenue,Total Silver Revenue," +
                    "Total Bronze Revenue,Total General Admission Revenue");
            
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a");
            
            for (Event e : events.values()) {
                String type = e instanceof Sport ? "Sport"
                            : e instanceof Concert ? "Concert"
                            : e instanceof Festival ? "Festival"
                            : "Unknown";
                
                // Format all the data
                // Note: The format string has "0" literals and ",," for empty fields
                // Format: id,type,name,date,time,vip,gold,silver,bronze,ga,venue,0,venueType,capacity,cost,0,0,0,0,0,0,,fireworksCost,soldVIP,soldGold,soldSilver,soldBronze,soldGA,revVIP,revGold,revSilver,revBronze,revGA
                w.printf("%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s,0,%s,%d,%.2f,0,0,0,0,0,0,,%.2f,%d,%d,%d,%d,%d,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    e.getId(),
                    type,
                    e.getName(),
                    e.getDateTime().format(dateFmt),
                    e.getDateTime().format(timeFmt),
                    e.getPrice(TicketType.VIP),
                    e.getPrice(TicketType.GOLD),
                    e.getPrice(TicketType.SILVER),
                    e.getPrice(TicketType.BRONZE),
                    e.getPrice(TicketType.GENERAL_ADMISSION),
                    e.getVenue().getName(),
                    e.getVenue().getType(),
                    e.getVenue().getCapacity(),
                    e.getVenue().getCost(),
                    e.getFireworksCost(), // Fireworks cost (double)
                    // Sales data (sold() returns int, so use %d)
                    e.sold(TicketType.VIP),
                    e.sold(TicketType.GOLD),
                    e.sold(TicketType.SILVER),
                    e.sold(TicketType.BRONZE),
                    e.sold(TicketType.GENERAL_ADMISSION),
                    // Revenue data
                    e.revenue(TicketType.VIP),
                    e.revenue(TicketType.GOLD),
                    e.revenue(TicketType.SILVER),
                    e.revenue(TicketType.BRONZE),
                    e.revenue(TicketType.GENERAL_ADMISSION)
                );
            }
        }
    }

    /**
     * Writes updated customers to a CSV file.
     * Taken from Miranda Macias.
     * Enhanced to include additional customer fields from Brian Casio.
     * 
     * @param fn Filename to write to
     * @throws IOException if file cannot be written
     */
    public void writeUpdatedCustomers(String fn) throws IOException {
        try (PrintWriter w = new PrintWriter(fn)) {
            w.println("ID,First Name,Last Name,Money Available,Concerts Purchased," +
                    "TicketMiner Membership,Username,Password,Total Invoices");
            
            int id = 1;
            for (Customer c : customers.values()) {
                String[] names = c.getFullName().split(" ", 2);
                String firstName = names.length > 0 ? names[0] : "";
                String lastName = names.length > 1 ? names[1] : "";
                
                w.printf("%d,%s,%s,%.2f,0,%s,%s,******,%d%n",
                    c.getId() != 0 ? c.getId() : id++,
                    c.getFirstName() != null && !c.getFirstName().isEmpty() ? 
                        c.getFirstName() : firstName,
                    c.getLastName() != null && !c.getLastName().isEmpty() ? 
                        c.getLastName() : lastName,
                    c.getBalance(),
                    c.hasTicketMinerMembership(),
                    c.getUsername(),
                    c.getInvoices().size()
                );
            }
        }
    }
}

