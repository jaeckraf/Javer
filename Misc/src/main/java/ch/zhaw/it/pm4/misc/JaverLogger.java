package ch.zhaw.it.pm4.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience facade around SLF4J that logs under the caller class instead of
 * under the facade class itself.
 */
public final class JaverLogger {

    private JaverLogger() {
    }

    /**
     * Logs a debug message.
     *
     * @param message message text
     */
    public static void debug(String message) {
        resolveLogger().debug(message);
    }

    /**
     * Logs a debug message with a throwable.
     *
     * @param message message text
     * @param throwable related throwable
     */
    public static void debug(String message, Throwable throwable) {
        resolveLogger().debug(message, throwable);
    }

    /**
     * Logs an informational message.
     *
     * @param message message text
     */
    public static void info(String message) {
        resolveLogger().info(message);
    }

    /**
     * Logs an informational message with a throwable.
     *
     * @param message message text
     * @param throwable related throwable
     */
    public static void info(String message, Throwable throwable) {
        resolveLogger().info(message, throwable);
    }

    /**
     * Logs a warning message.
     *
     * @param message message text
     */
    public static void warning(String message) {
        resolveLogger().warn(message);
    }

    /**
     * Logs a warning message with a throwable.
     *
     * @param message message text
     * @param throwable related throwable
     */
    public static void warning(String message, Throwable throwable) {
        resolveLogger().warn(message, throwable);
    }

    /**
     * Logs an error message.
     *
     * @param message message text
     */
    public static void error(String message) {
        resolveLogger().error(message);
    }

    /**
     * Logs an error message with a throwable.
     *
     * @param message message text
     * @param throwable related throwable
     */
    public static void error(String message, Throwable throwable) {
        resolveLogger().error(message, throwable);
    }

    private static Logger resolveLogger() {
        return LoggerFactory.getLogger(resolveCallerClassName());
    }

    private static String resolveCallerClassName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .filter(frame -> frame.getDeclaringClass() != JaverLogger.class)
                        .findFirst()
                        .map(frame -> frame.getDeclaringClass().getName())
                        .orElse(JaverLogger.class.getName()));
    }
}
