/**
 * Arena subclass of Venue.
 * Base code taken from teammb (Miranda Macias & Brian Casio)
 * Type interface implementation added from teamgd
 */
public class Arena extends Venue implements Type {
    /**
     * Constructor for Arena venue.
     * 
     * @param id Unique identifier for the arena
     * @param name Name of the arena
     * @param address Address of the arena
     * @param capacity Maximum capacity of the arena
     * @param cost Cost of the arena for profit calculations
     */
    public Arena(String id, String name, String address, int capacity, double cost) {
        super(id, name, address, capacity, cost);
    }
    
    /**
     * String representation of the arena.
     * Taken from Miranda Macias.
     * 
     * @return Formatted string with "Arena:" prefix
     */
    @Override
    public String toString() {
        return "Arena: " + super.toString();
    }
}

