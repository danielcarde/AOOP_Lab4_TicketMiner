import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating Event objects.
 * 
 * Structure taken from Miranda Macias:
 * - Uses LocalDateTime and Map<TicketType, Double>
 * - Static create() method with switch expression
 * - Supports extra parameters for Sport and Festival
 * 
 * This factory follows the Factory Pattern to encapsulate object creation logic
 * and make the system more maintainable.
 */
public class EventFactory {
    /**
     * Creates an Event object based on the event type.
     * Taken from Miranda Macias.
     * 
     * @param type Type of event ("Sport", "Concert", or "Festival")
     * @param id Event ID
     * @param name Event name
     * @param dt Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     * @param sportType Type of sport (for Sport events)
     * @param teams List of teams (for Sport events)
     * @param startDate Start date (for Festival events)
     * @param endDate End date (for Festival events)
     * @param lineup List of artists (for Festival events)
     * @return The created Event object
     * @throws IllegalArgumentException if the event type is unknown
     */
    public static Event create(
        String type,
        String id,
        String name,
        LocalDateTime dt,
        Venue venue,
        Map<TicketType, Double> prices,
        String sportType, 
        List<String> teams,
        LocalDate startDate, 
        LocalDate endDate, 
        List<String> lineup
    ) {
        return switch(type) {
            case "Sport" ->
                new Sport(id, name, dt, venue, prices, sportType, teams);
            case "Concert" ->
                new Concert(id, name, dt, venue, prices);
            case "Festival" ->
                new Festival(id, name, dt, venue, prices, startDate, endDate, lineup);
            default -> throw new IllegalArgumentException("Unknown type " + type);
        };
    }
}

