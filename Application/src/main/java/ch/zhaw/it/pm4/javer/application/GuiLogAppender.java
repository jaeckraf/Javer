package ch.zhaw.it.pm4.javer.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;

import java.util.function.Consumer;

public class GuiLogAppender extends AppenderBase<ILoggingEvent> {

    private static volatile Consumer<String> consumer;

    public static void setConsumer(Consumer<String> guiConsumer) {
        consumer = guiConsumer;
    }

    public static void clearConsumer() {
        consumer = null;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        Consumer<String> currentConsumer = consumer;
        if (currentConsumer == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(eventObject.getFormattedMessage()).append(System.lineSeparator());

        if (eventObject.getThrowableProxy() != null) {
            sb.append(eventObject.getThrowableProxy().getClassName())
                    .append(": ")
                    .append(eventObject.getThrowableProxy().getMessage())
                    .append(System.lineSeparator());
        }

        String text = sb.toString();

        Platform.runLater(() -> currentConsumer.accept(text));
    }
}