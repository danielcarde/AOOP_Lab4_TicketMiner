import java.time.LocalDateTime;
import java.util.Map;

/**
 * Concert subclass of Event.
 * 
 * Structure taken from Miranda Macias:
 * - Uses LocalDateTime, Map<TicketType, Double>
 * 
 * Additional functionality taken from Brian Casio:
 * - artistName field and getter
 * - displayEventDetails() override to show artist name
 */
public class Concert extends Event {
    private String artistName;

    /**
     * Constructor for Concert event.
     * Taken from Miranda Macias (base structure).
     * 
     * @param id Event ID
     * @param name Event name
     * @param dateTime Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     */
    public Concert(String id, String name, LocalDateTime dateTime, Venue venue, 
                   Map<TicketType, Double> prices) {
        super(id, name, dateTime, venue, prices, "Concert");
        this.artistName = ""; // Default empty, can be set separately if needed
    }

    /**
     * Constructor for Concert event with artist name.
     * Taken from Brian Casio (for compatibility with code expecting artist name).
     * 
     * @param id Event ID
     * @param name Event name
     * @param dateTime Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     * @param artistName Name of the performing artist
     */
    public Concert(String id, String name, LocalDateTime dateTime, Venue venue, 
                   Map<TicketType, Double> prices, String artistName) {
        super(id, name, dateTime, venue, prices, "Concert");
        this.artistName = artistName;
    }

    /**
     * Gets the artist name.
     * Taken from Brian Casio.
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * Sets the artist name.
     * Helper method for flexibility.
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * Displays event details with artist information.
     * Taken from Brian Casio.
     */
    @Override
    public void displayEventDetails() {
        super.displayEventDetails();
        if (artistName != null && !artistName.isEmpty()) {
            System.out.println("Artist: " + artistName);
        }
    }

    /**
     * String representation of the concert event.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        String baseString = "Concert: " + super.toString();
        if (artistName != null && !artistName.isEmpty()) {
            return baseString + " - " + artistName;
        }
        return baseString;
    }
}

