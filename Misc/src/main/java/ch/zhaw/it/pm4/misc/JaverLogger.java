package ch.zhaw.it.pm4.misc;

import java.io.IOException;
import java.util.logging.*;

public class JaverLogger {

    private static final Logger LOGGER = Logger.getLogger("JaverLogger");

    static {
        try {
            // Disable default console handler (optional)
            LOGGER.setUseParentHandlers(false);

            // ---- File Handler ----
            FileHandler fileHandler = new FileHandler("javer.log", false);
            fileHandler.setLevel(Level.ALL);

            // Custom format
            fileHandler.setFormatter(new SimpleFormatter());

            // Add handlers
            LOGGER.addHandler(fileHandler);

            // Global log level
            LOGGER.setLevel(Level.ALL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- Public static methods ----

    public static void debug(String message) {
        LOGGER.finest(message);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }

}