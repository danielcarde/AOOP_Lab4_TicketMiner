/**
 * Stadium subclass of Venue.
 * Base code taken from teammb (Miranda Macias & Brian Casio)
 * Type interface implementation added from teamgd
 */
public class Stadium extends Venue implements Type {
    /**
     * Constructor for Stadium venue.
     * 
     * @param id Unique identifier for the stadium
     * @param name Name of the stadium
     * @param address Address of the stadium
     * @param capacity Maximum capacity of the stadium
     * @param cost Cost of the stadium for profit calculations
     */
    public Stadium(String id, String name, String address, int capacity, double cost) {
        super(id, name, address, capacity, cost);
    }
}

