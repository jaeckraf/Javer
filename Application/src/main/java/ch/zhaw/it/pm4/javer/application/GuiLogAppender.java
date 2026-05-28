package ch.zhaw.it.pm4.javer.application;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.function.Consumer;

/**
 * Logback appender that forwards formatted application log events to a GUI
 * text consumer.
 */
public class GuiLogAppender extends AppenderBase<ILoggingEvent> {

    private static volatile Consumer<String> consumer;
    private String pattern;
    private PatternLayout layout;

    /**
     * Creates a Logback appender instance.
     */
    public GuiLogAppender() {
        // Required by JavaFX for reflective instantiation; must remain empty.
    }

    /**
     * Registers the active GUI consumer for formatted log lines.
     *
     * @param guiConsumer consumer invoked for each formatted log event
     */
    public static void setConsumer(Consumer<String> guiConsumer) {
        consumer = guiConsumer;
    }

    /**
     * Disconnects the current GUI consumer so later log events are ignored by
     * this appender.
     */
    public static void clearConsumer() {
        consumer = null;
    }

    /**
     * Sets the Logback pattern configured from {@code logback.xml}.
     *
     * @param pattern pattern used to format GUI log output
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void start() {
        if (pattern == null || pattern.isBlank()) {
            addError("No pattern configured for GUI log appender.");
            return;
        }

        layout = new PatternLayout();
        layout.setContext(getContext());
        layout.setPattern(pattern);
        layout.start();
        super.start();
    }

    @Override
    public void stop() {
        if (layout != null) {
            layout.stop();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        Consumer<String> currentConsumer = consumer;
        if (currentConsumer == null || layout == null) {
            return;
        }

        String text = layout.doLayout(eventObject);
        currentConsumer.accept(text);
    }
}
