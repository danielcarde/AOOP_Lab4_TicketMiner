/**
 * Abstract base class for venues where events are held.
 * 
 * Structure taken from Miranda Macias
 * Additional fields (cost, type) taken from Brian Casio
 * This combines the venue identification system from Miranda with the
 * cost tracking capability from Brian for profit calculations.
 */
public abstract class Venue {
    private String id;
    private String name;
    private String address;
    private int capacity;
    private double cost;  // Taken from p1-briancasio for profit calculations

    /**
     * Protected constructor for venue subclasses.
     * 
     * @param id Unique identifier for the venue
     * @param name Name of the venue
     * @param address Address of the venue
     * @param capacity Maximum capacity of the venue
     * @param cost Cost of the venue for profit calculations
     */
    protected Venue(String id, String name, String address, int capacity, double cost) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.cost = cost;
    }

    /**
     * Returns the unique venue identifier.
     * 
     * @return The venue ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the venue's name.
     * 
     * @return The venue name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the venue's address.
     * 
     * @return The venue address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the maximum number of people the venue can hold.
     * 
     * @return The venue capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the cost of the venue for profit calculations.
     * Taken from Brian Casio.
     * 
     * @return The venue cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Gets the venue type as a string (e.g., "Stadium", "Arena").
     * Taken from Brian Casio.
     * 
     * @return The venue type name
     */
    public String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * String representation of the venue.
     * 
     * @return Formatted string with venue details
     */
    @Override
    public String toString() {
        return String.format(
            "%s (ID: %s)%nAddress: %s%nCapacity: %,d%nCost: $%.2f",
            name, id, address, capacity, cost
        );
    }
}

