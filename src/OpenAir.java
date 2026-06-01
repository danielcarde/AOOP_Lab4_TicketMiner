/**
 * OpenAir subclass of Venue.
 * Base code taken from teammb (Miranda Macias & Brian Casio)
 * Type interface implementation added from teamgd
 */
public class OpenAir extends Venue implements Type {
    /**
     * Constructor for OpenAir venue.
     * 
     * @param id Unique identifier for the open air venue
     * @param name Name of the open air venue
     * @param address Address of the open air venue
     * @param capacity Maximum capacity of the open air venue
     * @param cost Cost of the open air venue for profit calculations
     */
    public OpenAir(String id, String name, String address, int capacity, double cost) {
        super(id, name, address, capacity, cost);
    }

    /**
     * String representation of the open air venue.
     * Taken from Miranda Macias.
     * 
     * @return Formatted string with "OpenAir:" prefix
     */
    @Override
    public String toString() {
        return "OpenAir: " + super.toString();
    }
}

