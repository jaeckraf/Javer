package ch.zhaw.it.pm4.javer.application;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.function.Consumer;

public class GuiLogAppender extends AppenderBase<ILoggingEvent> {

    private static volatile Consumer<String> consumer;
    private String pattern;
    private PatternLayout layout;

    public static void setConsumer(Consumer<String> guiConsumer) {
        consumer = guiConsumer;
    }

    public static void clearConsumer() {
        consumer = null;
    }

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
