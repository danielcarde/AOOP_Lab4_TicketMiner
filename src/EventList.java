/**
 * EventList class - Taken from teamgd (Daniel and Gabriel)
 * This is an alternative singleton implementation for managing events.
 * Note: This uses a different structure than DataStore from teammb.
 * The base code uses DataStore, but this class is included as an alternative from teamgd.
 * 
 * IMPORTANT: This class expects Event classes to have methods like:
 * - toCSVString() - for CSV export
 * - inquireEventDetails(String) - for admin inquiry
 * These methods may need to be added to the Event classes if they don't exist.
 */

import java.util.*;
import java.io.*;

public final class EventList {
    private static volatile EventList instance;
    private Map<Integer, Sport> sportEvents;
    private Map<Integer, Concert> concertEvents;
    private Map<Integer, Festival> festivalEvents;
    private Map<String, Venue> venueList;
    private int largestEventID;

    private static final String fileName = "EventListUpdated.csv";

    private EventList() {
        sportEvents = new HashMap<>();
        concertEvents = new HashMap<>();
        festivalEvents = new HashMap<>();
        venueList = new HashMap<>();
        this.largestEventID = 0;
    }

    public static EventList getInstance() {
        if (instance == null) {
            synchronized (EventList.class) {
                if (instance == null) {
                    instance = new EventList();
                }
            }
        }
        return instance;
    }

    public void loadEventsFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("CSV file is empty.");
                return;
            }

            String[] headers = headerLine.split(",", -1);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim(), i);
            }

            String currLine;
            while ((currLine = br.readLine()) != null) {
                if (currLine.isBlank() || !Character.isDigit(currLine.charAt(0))) {
                    continue;
                }

                String[] details = currLine.split(",", -1);

                try {
                    int id = Integer.parseInt(getValue(details, headerMap, "Event ID", "0"));

                    if (id > largestEventID) {
                        largestEventID = id;
                    }

                    String eventType = getValue(details, headerMap, "Event Type", "");
                    String name = getValue(details, headerMap, "Name", "");
                    if (eventType.isEmpty() || name.isEmpty()) {
                        System.out.println("Skipping row due to missing critical fields: " + currLine);
                        continue;
                    }
                    String date = getValue(details, headerMap, "Date", "");
                    String time = getValue(details, headerMap, "Time", "");
                    double VIPPrice = Double.parseDouble(getValue(details, headerMap, "VIP Price", "0"));
                    double goldPrice = Double.parseDouble(getValue(details, headerMap, "Gold Price", "0"));
                    double silverPrice = Double.parseDouble(getValue(details, headerMap, "Silver Price", "0"));
                    double bronzePrice = Double.parseDouble(getValue(details, headerMap, "Bronze Price", "0"));
                    double generalAdPrice = Double
                            .parseDouble(getValue(details, headerMap, "General Admission Price", "0"));
                    String venueName = getValue(details, headerMap, "Venue Name", "");
                    double pctSeatsUnavailable = Double
                            .parseDouble(getValue(details, headerMap, "Pct Seats Unavailable", "0"));
                    String venueType = getValue(details, headerMap, "Venue Type", "");
                    int capacity = Integer.parseInt(getValue(details, headerMap, "Capacity", "0"));
                    double cost = Double.parseDouble(getValue(details, headerMap, "Cost", "0"));
                    double vipPct = Double.parseDouble(getValue(details, headerMap, "VIP Pct", "0"));
                    double goldPct = Double.parseDouble(getValue(details, headerMap, "Gold Pct", "0"));
                    double silverPct = Double.parseDouble(getValue(details, headerMap, "Silver Pct", "0"));
                    double bronzePct = Double.parseDouble(getValue(details, headerMap, "Bronze Pct", "0"));
                    double generalAdPct = Double
                            .parseDouble(getValue(details, headerMap, "General Admission Pct", "0"));
                    double reservedExtraPct = Double
                            .parseDouble(getValue(details, headerMap, "Reserved Extra Pct", "0"));
                    boolean fireworksPlanned = Boolean
                            .parseBoolean(getValue(details, headerMap, "Fireworks Planned", "false"));
                    double fireworksCost = Double.parseDouble(getValue(details, headerMap, "Fireworks Cost", "0"));

                    Venue venue;
                    if (venueList.containsKey(venueName)) {
                        venue = venueList.get(venueName);
                    } else {
                        // Note: This code expects Venue constructors with many parameters
                        // The teammb Venue class may have a different constructor signature
                        switch (venueType) {
                            case "Arena":
                                venue = new Arena(venueName, venueName, "", capacity, cost);
                                break;
                            case "Auditorium":
                                venue = new Auditorium(venueName, venueName, "", capacity, cost);
                                break;
                            case "Open Air":
                                venue = new OpenAir(venueName, venueName, "", capacity, cost);
                                break;
                            case "Stadium":
                                venue = new Stadium(venueName, venueName, "", capacity, cost);
                                break;
                            case "Field":
                                venue = new Field(venueName, venueName, "", capacity, cost);
                                break;
                            case "Outdoor":
                                venue = new Outdoor(venueName, venueName, "", capacity, cost);
                                break;
                            default:
                                System.out.println("Unknown venue type: " + venueType + " for row: " + currLine);
                                continue;
                        }
                        venueList.put(venueName, venue);
                    }

                    // Note: This code expects Event constructors with specific parameters
                    // The teammb Event classes may have different constructor signatures
                    switch (eventType) {
                        case "Sport":
                            // Sport sport = new Sport(id, name, date, time, VIPPrice, goldPrice, silverPrice, bronzePrice,
                            //         generalAdPrice, venue);
                            // sportEvents.put(id, sport);
                            System.out.println("Note: Sport event creation may need adaptation for teammb Event structure");
                            break;
                        case "Concert":
                            // Concert concert = new Concert(id, name, date, time, VIPPrice, goldPrice, silverPrice,
                            //         bronzePrice, generalAdPrice, venue);
                            // concertEvents.put(id, concert);
                            System.out.println("Note: Concert event creation may need adaptation for teammb Event structure");
                            break;
                        case "Festival":
                            // Festival festival = new Festival(id, name, date, time, VIPPrice, goldPrice, silverPrice,
                            //         bronzePrice, generalAdPrice, venue);
                            // festivalEvents.put(id, festival);
                            System.out.println("Note: Festival event creation may need adaptation for teammb Event structure");
                            break;
                        default:
                            System.out.println("Unknown event type: " + eventType + " for row: " + currLine);
                            continue;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Skipping row due to parsing error: " + currLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Sport> getSportEvents() {
        return sportEvents;
    }

    public Map<Integer, Concert> getConcertEvents() {
        return concertEvents;
    }

    public Map<Integer, Festival> getFestivalEvents() {
        return festivalEvents;
    }

    public Sport searchSportByID(int id) {
        return sportEvents.getOrDefault(id, null);
    }

    public Concert searchConcertByID(int id) {
        return concertEvents.getOrDefault(id, null);
    }

    public Festival searchFestivalByID(int id) {
        return festivalEvents.getOrDefault(id, null);
    }

    public void printSportEvents() {
        List<Integer> ids = new ArrayList<>(sportEvents.keySet());
        Collections.sort(ids);
        System.out.println("----------Sport Events----------");
        for (int id : ids) {
            System.out.println("ID: " + id + " Name: \t" + sportEvents.get(id).getName());
        }
        System.out.println("--------------------------------");
    }

    public void printConcertEvents() {
        List<Integer> ids = new ArrayList<>(concertEvents.keySet());
        Collections.sort(ids);
        System.out.println("----------Concert Events----------");
        for (int id : ids) {
            System.out.println("ID: " + id + " Name: \t" + concertEvents.get(id).getName());
        }
        System.out.println("----------------------------------");
    }

    public void printFestivalEvents() {
        List<Integer> ids = new ArrayList<>(festivalEvents.keySet());
        Collections.sort(ids);
        System.out.println("----------Festival Events----------");
        for (int id : ids) {
            System.out.println("ID: " + id + " Name: \t" + festivalEvents.get(id).getName());
        }
        System.out.println("-----------------------------------");
    }

    public Sport searchSportByName(String name) {
        for (Sport sport : sportEvents.values()) {
            if (sport.getName().equalsIgnoreCase(name)) {
                return sport;
            }
        }
        return null;
    }

    public Concert searchConcertByName(String name) {
        for (Concert concert : concertEvents.values()) {
            if (concert.getName().equalsIgnoreCase(name)) {
                return concert;
            }
        }
        return null;
    }

    public Festival searchFestivalByName(String name) {
        for (Festival festival : festivalEvents.values()) {
            if (festival.getName().equalsIgnoreCase(name)) {
                return festival;
            }
        }
        return null;
    }

    public void writeEventsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(
                    "ID,Event Type,Name,Date,Time,VIPPrice,GoldPrice,SilverPrice,BronzePrice,GeneralAdPrice,VenueName,PctSeatsUnavailable,VenueType,Capacity,Cost,VIPPct,GoldPct,SilverPct,BronzePct,GeneralAdPct,ReservedExtraPct,FireworksPlanned,FireworksCost,"
                            +
                            "VIP Seats Sold,Gold Seats Sold,Silver Seats Sold,Bronze Seats Sold,General Admission Seats Sold,"
                            +
                            "Total VIP Revenue,Total Gold Revenue,Total Silver Revenue,Total Bronze Revenue,Total General Admission Revenue");
            writer.newLine();

            List<Event> allEvents = new ArrayList<>();
            allEvents.addAll(sportEvents.values());
            allEvents.addAll(concertEvents.values());
            allEvents.addAll(festivalEvents.values());
            allEvents.sort(Comparator.comparing(Event::getId));

            for (Event event : allEvents) {
                // Note: This expects Event.toCSVString() method which may not exist in teammb Event class
                // writer.write(event.toCSVString());
                // writer.newLine();
                System.out.println("Note: CSV writing may need adaptation - Event.toCSVString() may not exist");
            }

            System.out.println("All events successfully written to CSV on logout!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getValue(String[] details, Map<String, Integer> headerMap, String column, String defaultValue) {
        Integer i = headerMap.get(column);
        if (i == null || i >= details.length || details[i].trim().isEmpty()) {
            return defaultValue;
        }
        return details[i].trim();
    }

    /**
     * VenueList management - Taken from teamgd
     */
    public Map<String, Venue> getVenueList() {
        return venueList;
    }

    public Venue getVenueByName(String name) {
        Venue venue = venueList.get(name);
        if (venue == null) {
            System.out.println("Venue not found: " + name);
        }
        return venue;
    }

    public void createEvent(int id, String type, String name, String date, String time, double VIPPrice,
            double goldPrice, double silverPrice, double bronzePrice, double generalAdPrice, String venueName) {

        Venue venue = getVenueByName(venueName);
        if (venue == null) {
            System.out.println("Cannot create event. Venue does not exist: " + venueName);
            return;
        }

        // Note: Event creation may need adaptation for teammb Event structure
        switch (type) {
            case "Sport":
                // Sport sport = new Sport(id, name, date, time, VIPPrice, goldPrice, silverPrice, bronzePrice,
                //         generalAdPrice, venue);
                // sportEvents.put(id, sport);
                System.out.println("Note: Sport event creation may need adaptation");
                break;
            case "Concert":
                // Concert concert = new Concert(id, name, date, time, VIPPrice, goldPrice, silverPrice, bronzePrice,
                //         generalAdPrice, venue);
                // concertEvents.put(id, concert);
                System.out.println("Note: Concert event creation may need adaptation");
                break;
            case "Festival":
                // Festival festival = new Festival(id, name, date, time, VIPPrice, goldPrice, silverPrice, bronzePrice,
                //         generalAdPrice, venue);
                // festivalEvents.put(id, festival);
                System.out.println("Note: Festival event creation may need adaptation");
                break;
            default:
                System.out.println("Unknown event type: " + type);
                return;
        }
    }

    public int getNextEventID() {
        largestEventID++;
        return largestEventID;
    }
}

