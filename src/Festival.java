import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Festival subclass of Event.
 * 
 * Structure taken from Miranda Macias:
 * - Uses LocalDateTime, Map<TicketType, Double>
 * - LocalDate startDate and endDate
 * - List<String> lineup
 * 
 * Additional functionality taken from Brian Casio:
 * - numberOfDays field and calculation
 * - hasMultipleStages field
 * - displayEventDetails() override
 */
public class Festival extends Event {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<String> lineup;
    
    // Additional fields from Brian Casio
    private int numberOfDays;
    private boolean hasMultipleStages;

    /**
     * Constructor for Festival event.
     * Taken from Miranda Macias (base structure).
     * Enhanced with fields from Brian Casio.
     * 
     * @param id Event ID
     * @param name Event name
     * @param dateTime Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     * @param startDate Start date of the festival
     * @param endDate End date of the festival
     * @param lineup List of artists/performers in the lineup
     */
    public Festival(String id, String name, LocalDateTime dateTime, Venue venue, 
                    Map<TicketType, Double> prices, LocalDate startDate, 
                    LocalDate endDate, List<String> lineup) {
        super(id, name, dateTime, venue, prices, "Festival");
        this.startDate = startDate;
        this.endDate = endDate;
        this.lineup = lineup;
        
        // Calculate numberOfDays from dates (from Brian Casio)
        if (startDate != null && endDate != null) {
            this.numberOfDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } else {
            this.numberOfDays = 1;
        }
        this.hasMultipleStages = false; // Default, can be set if needed
    }

    /**
     * Constructor for Festival with additional parameters from Brian Casio.
     * 
     * @param id Event ID
     * @param name Event name
     * @param dateTime Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     * @param startDate Start date of the festival
     * @param endDate End date of the festival
     * @param lineup List of artists/performers in the lineup
     * @param numberOfDays Number of days the festival lasts
     * @param hasMultipleStages Whether the festival has multiple stages
     */
    public Festival(String id, String name, LocalDateTime dateTime, Venue venue, 
                    Map<TicketType, Double> prices, LocalDate startDate, 
                    LocalDate endDate, List<String> lineup, int numberOfDays, 
                    boolean hasMultipleStages) {
        super(id, name, dateTime, venue, prices, "Festival");
        this.startDate = startDate;
        this.endDate = endDate;
        this.lineup = lineup;
        this.numberOfDays = numberOfDays;
        this.hasMultipleStages = hasMultipleStages;
    }

    /**
     * Gets the start date of the festival.
     * Taken from Miranda Macias.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date of the festival.
     * Taken from Miranda Macias.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the lineup of artists/performers.
     * Taken from Miranda Macias.
     */
    public List<String> getLineup() {
        return lineup;
    }

    /**
     * Gets the number of days the festival lasts.
     * Taken from Brian Casio.
     */
    public int getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * Checks if the festival has multiple stages.
     * Taken from Brian Casio.
     */
    public boolean hasMultipleStages() {
        return hasMultipleStages;
    }

    /**
     * Sets whether the festival has multiple stages.
     * Helper method.
     */
    public void setHasMultipleStages(boolean hasMultipleStages) {
        this.hasMultipleStages = hasMultipleStages;
    }

    /**
     * Displays event details with festival-specific information.
     * Taken from Brian Casio.
     */
    @Override
    public void displayEventDetails() {
        super.displayEventDetails();
        System.out.println("Duration: " + numberOfDays + " day(s)");
        if (hasMultipleStages) {
            System.out.println("Multiple Stages: Yes");
        } else {
            System.out.println("Multiple Stages: No");
        }
        if (lineup != null && !lineup.isEmpty()) {
            System.out.println("Lineup: " + String.join(", ", lineup));
        }
    }

    /**
     * String representation of the festival event.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        return String.format(
            "Festival: %s%nDates: %s-%s%nVenue: %s%nLineup: %s%nPrices: %s",
            getName(), 
            startDate != null ? startDate.toString() : "N/A",
            endDate != null ? endDate.toString() : "N/A",
            getVenue().getName(),
            lineup != null ? String.join(", ", lineup) : "None",
            getPriceMap()
        );
    }
}

