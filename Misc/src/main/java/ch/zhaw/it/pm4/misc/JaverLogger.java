package ch.zhaw.it.pm4.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JaverLogger {

    private JaverLogger() {}

    private static Logger getLogger() {
        // Caller-Klasse automatisch bestimmen
        String className = StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(1)
                        .findFirst()
                        .map(f -> f.getClassName())
                        .orElse("Unknown"));
        return LoggerFactory.getLogger(className);
    }

    public static void debug(String msg) {
        getLogger().trace(trim(msg));
    }

    public static void info(String msg) {
        getLogger().info(trim(msg));
    }

    public static void warning(String msg) {
        getLogger().warn(trim(msg));
    }

    public static void error(String msg) {
        getLogger().error(trim(msg));
    }

    public static void error(String msg, Throwable t) {
        getLogger().error(trim(msg), t);
    }

    private static String trim(String msg) {
        if (msg == null) return "";
        if (msg.length() <= 200) return msg;
        return msg.substring(0, 197) + "...";
    }
}