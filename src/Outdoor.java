/**
 * Outdoor venue class - Taken from teamgd (Daniel and Gabriel)
 * This is an additional venue type not present in teammb base code.
 */
public class Outdoor extends Venue implements Type {
    /**
     * Constructor for Outdoor venue.
     * Taken from teamgd.
     */
    public Outdoor(String id, String name, String address, int capacity, double cost) {
        super(id, name, address, capacity, cost);
    }

    /**
     * Returns the venue type as a string.
     * Taken from teamgd.
     */
    @Override
    public String getType() {
        return "Outdoor";
    }
}

