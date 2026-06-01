import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Event Manager for creating new events from console.
 * PA3 Requirement 7.
 * 
 * Authored by Miranda Macias & Brian Casio for PA3
 */
public class EventManager {
    private static int largestEventId = 0;  // Track largest ID for auto-generation
    
    /**
     * Initialize the largest event ID from existing events.
     */
    public static void initializeLargestEventId(Collection<Event> events) {
        for (Event e : events) {
            try {
                int id = Integer.parseInt(e.getId());
                if (id > largestEventId) {
                    largestEventId = id;
                }
            } catch (NumberFormatException ex) {
                // Skip non-numeric IDs
            }
        }
    }
    
    /**
     * Creates a new event from console input.
     */
    public static Event createEventFromConsole(Scanner scanner, Map<String, Venue> venues) {
        System.out.println("\n=== Create New Event ===");
        
        // Auto-generate Event ID
        largestEventId++;
        String eventId = String.valueOf(largestEventId);
        System.out.println("Event ID (auto-generated): " + eventId);
        
        // Get Event Name
        System.out.print("Event Name: ");
        String eventName = scanner.nextLine().trim();
        
        // Get Event Date (MM/DD/YYYY)
        System.out.print("Event Date (MM/DD/YYYY): ");
        String dateStr = scanner.nextLine().trim();
        
        // Get Event Time (HH:MM AM/PM)
        System.out.print("Event Time (HH:MM AM or PM): ");
        String timeStr = scanner.nextLine().trim();
        
        // Parse date and time
        LocalDateTime dateTime;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
            dateTime = LocalDateTime.parse(dateStr + " " + timeStr, dtf);
        } catch (Exception e) {
            System.out.println("Invalid date/time format. Using current date/time.");
            dateTime = LocalDateTime.now();
        }
        
        // Select Venue
        System.out.println("\nSelect Venue:");
        System.out.println("1. Sun Bowl Stadium");
        System.out.println("2. Don Haskins Center");
        System.out.println("3. Magoffin Auditorium");
        System.out.println("4. San Jacinto Plaza");
        System.out.println("5. Centennial Plaza");
        System.out.print("Choice (1-5): ");
        
        int venueChoice = 0;
        try {
            venueChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            venueChoice = 1;
        }
        
        Venue venue = selectVenue(venueChoice, venues);
        if (venue == null) {
            System.out.println("Invalid venue selection.");
            return null;
        }
        
        // Get General Admission Price (max $4000)
        System.out.print("General Admission Price (max $4000): $");
        double gaPrice = 0;
        try {
            gaPrice = Double.parseDouble(scanner.nextLine().trim());
            if (gaPrice > 4000) {
                gaPrice = 4000;
                System.out.println("Price capped at $4000");
            }
        } catch (NumberFormatException ex) {
            gaPrice = 50.0;  // Default
        }
        
        // Calculate other prices programmatically
        Map<TicketType, Double> prices = new HashMap<>();
        prices.put(TicketType.VIP, gaPrice * 5.0);
        prices.put(TicketType.GOLD, gaPrice * 3.0);
        prices.put(TicketType.SILVER, gaPrice * 2.5);
        prices.put(TicketType.BRONZE, gaPrice * 1.5);
        prices.put(TicketType.GENERAL_ADMISSION, gaPrice);
        
        // Select Event Type
        System.out.println("\nSelect Event Type:");
        System.out.println("1. Sport");
        System.out.println("2. Concert");
        System.out.println("3. Festival");
        System.out.print("Choice (1-3): ");
        
        int typeChoice = 0;
        try {
            typeChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            typeChoice = 2;  // Default to Concert
        }
        
        String eventType;
        switch (typeChoice) {
            case 1:
                eventType = "Sport";
                break;
            case 3:
                eventType = "Festival";
                break;
            default:
                eventType = "Concert";
        }
        
        // PA3 Requirement 7c - Ask about fireworks
        System.out.print("\nWill this event have fireworks? (yes/no): ");
        String fireworksInput = scanner.nextLine().trim().toLowerCase();
        boolean hasFireworks = fireworksInput.startsWith("y");
        double fireworksCost = 0.0;
        
        if (hasFireworks) {
            System.out.print("Fireworks Cost: $");
            try {
                fireworksCost = Double.parseDouble(scanner.nextLine().trim());
                if (fireworksCost < 0) {
                    fireworksCost = 0.0;
                }
            } catch (NumberFormatException ex) {
                fireworksCost = 0.0;
            }
        }
        
        // Create event using factory
        Event newEvent = EventFactory.create(
            eventType,
            eventId,
            eventName,
            dateTime,
            venue,
            prices,
            "",  // sportType - can be enhanced
            List.of(),  // teams
            dateTime.toLocalDate(),  // startDate
            dateTime.toLocalDate(),  // endDate
            List.of()  // lineup
        );
        
        // Set fireworks information
        if (newEvent != null) {
            newEvent.setFireworks(hasFireworks, fireworksCost);
        }
        
        System.out.println("\n✓ Event created successfully!");
        System.out.println("Event ID: " + eventId);
        System.out.println("Prices: VIP=$" + String.format("%.2f", prices.get(TicketType.VIP)) +
                         ", Gold=$" + String.format("%.2f", prices.get(TicketType.GOLD)) +
                         ", Silver=$" + String.format("%.2f", prices.get(TicketType.SILVER)) +
                         ", Bronze=$" + String.format("%.2f", prices.get(TicketType.BRONZE)) +
                         ", GA=$" + String.format("%.2f", gaPrice));
        
        return newEvent;
    }
    
    private static Venue selectVenue(int choice, Map<String, Venue> venues) {
        String venueName;
        int capacity;
        double cost;
        String venueType;
        
        switch (choice) {
            case 1:
                venueName = "Sun Bowl Stadium";
                capacity = 51500;
                cost = 20000.0;
                venueType = "Stadium";
                break;
            case 2:
                venueName = "Don Haskins Center";
                capacity = 12222;
                cost = 8000.0;
                venueType = "Arena";
                break;
            case 3:
                venueName = "Magoffin Auditorium";
                capacity = 700;
                cost = 2000.0;
                venueType = "Auditorium";
                break;
            case 4:
                venueName = "San Jacinto Plaza";
                capacity = 5000;
                cost = 5000.0;
                venueType = "OpenAir";
                break;
            case 5:
                venueName = "Centennial Plaza";
                capacity = 3000;
                cost = 3000.0;
                venueType = "OpenAir";
                break;
            default:
                return null;
        }
        
        // Check if venue already exists
        if (venues.containsKey(venueName)) {
            return venues.get(venueName);
        }
        
        // Create new venue
        Venue venue;
        switch (venueType) {
            case "Stadium":
                venue = new Stadium(venueName, venueName, "", capacity, cost);
                break;
            case "Arena":
                venue = new Arena(venueName, venueName, "", capacity, cost);
                break;
            case "Auditorium":
                venue = new Auditorium(venueName, venueName, "", capacity, cost);
                break;
            default:
                venue = new OpenAir(venueName, venueName, "", capacity, cost);
        }
        
        venues.put(venueName, venue);
        return venue;
    }
}