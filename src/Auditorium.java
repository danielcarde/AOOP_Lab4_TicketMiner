/**
 * Auditorium subclass of Venue.
 * Base code taken from teammb (Miranda Macias & Brian Casio)
 * Type interface implementation added from teamgd
 */
public class Auditorium extends Venue implements Type {
    /**
     * Constructor for Auditorium venue.
     * 
     * @param id Unique identifier for the auditorium
     * @param name Name of the auditorium
     * @param address Address of the auditorium
     * @param capacity Maximum capacity of the auditorium
     * @param cost Cost of the auditorium for profit calculations
     */
    public Auditorium(String id, String name, String address, int capacity, double cost) {
        super(id, name, address, capacity, cost);
    }
    
    /**
     * String representation of the auditorium.
     * Taken from Miranda Macias.
     * 
     * @return Formatted string with "Auditorium:" prefix
     */
    @Override
    public String toString() {
        return "Auditorium: " + super.toString();
    }
}

