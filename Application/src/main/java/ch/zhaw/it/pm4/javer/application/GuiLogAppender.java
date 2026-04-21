package ch.zhaw.it.pm4.javer.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import javafx.application.Platform;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class GuiLogAppender extends OutputStreamAppender<ILoggingEvent> {

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
        if (currentConsumer == null || encoder == null) {
            return;
        }

        byte[] bytes = encoder.encode(eventObject);
        String text = new String(bytes, StandardCharsets.UTF_8);

        Platform.runLater(() -> currentConsumer.accept(text));
    }

    @Override
    public void setEncoder(ch.qos.logback.core.encoder.Encoder<ILoggingEvent> encoder) {
        super.setEncoder(encoder);
    }

}