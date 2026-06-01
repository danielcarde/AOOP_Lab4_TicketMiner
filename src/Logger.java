import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Logger class for tracking system activities.
 * 
 * Taken from Miranda Macias
 * This class provides a centralized logging system that stores log entries
 * in memory and writes them to a file when flushed.
 */
public class Logger {
    private static final List<String> entries = new ArrayList<>();

    /**
     * Logs a message with timestamp.
     * Taken from Miranda Macias.
     * 
     * @param msg The message to log
     */
    public static void log(String msg) {
        entries.add(LocalDateTime.now() + " - " + msg);
    }

    /**
     * Writes all logged entries to a file.
     * Taken from Miranda Macias.
     * 
     * @param filename The name of the file to write to
     */
    public static void flushToFile(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            entries.forEach(out::println);
        } catch (IOException ex) {
            System.err.println("Unable to write log: " + ex.getMessage());
        }
    }
}

