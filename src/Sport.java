import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Sport subclass of Event.
 * 
 * Structure taken from Miranda Macias:
 * - Uses LocalDateTime, Map<TicketType, Double>, List<String> teams
 * 
 * Additional functionality taken from Brian Casio:
 * - displayEventDetails() override to show sport-specific info
 * - Separate homeTeam and awayTeam fields (converted from List teams)
 */
public class Sport extends Event {
    private final String sportType;
    private final List<String> teams;

    /**
     * Constructor for Sport event.
     * Taken from Miranda Macias.
     * 
     * @param id Event ID
     * @param name Event name
     * @param dateTime Date and time of the event
     * @param venue Venue where event takes place
     * @param prices Map of ticket type to price
     * @param sportType Type of sport (e.g., "Football", "Basketball")
     * @param teams List of teams playing (typically 2 teams)
     */
    public Sport(String id, String name, LocalDateTime dateTime, Venue venue, 
                 Map<TicketType, Double> prices, String sportType, List<String> teams) {
        super(id, name, dateTime, venue, prices, "Sport");
        this.sportType = sportType;
        this.teams = teams;
    }

    /**
     * Gets the sport type.
     * Taken from Miranda Macias.
     */
    public String getSportType() {
        return sportType;
    }

    /**
     * Gets the list of teams playing.
     * Taken from Miranda Macias.
     */
    public List<String> getTeams() {
        return teams;
    }

    /**
     * Gets the home team name.
     * Helper method combining functionality from both versions.
     * 
     * @return First team in the teams list, or empty string if none
     */
    public String getHomeTeam() {
        return teams.size() > 0 ? teams.get(0) : "";
    }

    /**
     * Gets the away team name.
     * Helper method combining functionality from both versions.
     * 
     * @return Second team in the teams list, or empty string if none
     */
    public String getAwayTeam() {
        return teams.size() > 1 ? teams.get(1) : "";
    }

    /**
     * Displays event details with sport-specific information.
     * Taken from Brian Casio (method signature and display logic).
     */
    @Override
    public void displayEventDetails() {
        super.displayEventDetails();
        System.out.println("Sport: " + sportType);
        if (teams.size() >= 2) {
            System.out.println("Teams: " + teams.get(0) + " vs " + teams.get(1));
        } else if (teams.size() == 1) {
            System.out.println("Team: " + teams.get(0));
        }
    }

    /**
     * String representation of the sport event.
     * Taken from Miranda Macias.
     */
    @Override
    public String toString() {
        String matchup = teams.size() >= 2
                       ? teams.get(0) + " vs " + teams.get(1)
                       : String.join(", ", teams);
        return String.format(
            "Sport: %s (%s) on %s%nVenue: %s%nPrices: %s",
            getName(),
            matchup,
            getDateTime(),
            getVenue().getName(),
            getPriceMap()
        );
    }
}

