package ch.zhaw.it.pm4.javer.application;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;

import java.util.function.Consumer;

public class GuiLogAppender extends AppenderBase<ILoggingEvent> {

    private static volatile Consumer<String> consumer;
    private PatternLayout layout;

    public static void setConsumer(Consumer<String> guiConsumer) {
        consumer = guiConsumer;
    }

    public static void clearConsumer() {
        consumer = null;
    }

    @Override
    public void start() {
        layout = new PatternLayout();
        layout.setContext(getContext());
        layout.setPattern("%d{HH:mm:ss} %-7level %-24.24logger{0} %-24method %msg%n");
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
        Platform.runLater(() -> currentConsumer.accept(text));
    }
}