package ch.zhaw.it.pm4.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JaverLogger {

    private JaverLogger() {
    }

    public static void debug(String message) {
        resolveLogger().debug(message);
    }

    public static void debug(String message, Throwable throwable) {
        resolveLogger().debug(message, throwable);
    }

    public static void info(String message) {
        resolveLogger().info(message);
    }

    public static void info(String message, Throwable throwable) {
        resolveLogger().info(message, throwable);
    }

    public static void warning(String message) {
        resolveLogger().warn(message);
    }

    public static void warning(String message, Throwable throwable) {
        resolveLogger().warn(message, throwable);
    }

    public static void error(String message) {
        resolveLogger().error(message);
    }

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