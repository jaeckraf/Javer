package ch.zhaw.it.pm4.javer.compiler.diagnostics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JaverLoggerTest {

    private static Logger logger;
    private static TestLogHandler testHandler;

    // Custom handler to capture logs
    static class TestLogHandler extends Handler {
        private final List<LogRecord> logs = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            logs.add(record);
        }

        @Override public void flush() {}
        @Override public void close() throws SecurityException {}

        public List<LogRecord> getLogs() {
            return logs;
        }
    }

    @BeforeAll
    static void setup() {
        logger = Logger.getLogger("JaverLogger");

        testHandler = new TestLogHandler();
        testHandler.setLevel(Level.ALL);

        logger.addHandler(testHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void clearLogs() {
        testHandler.getLogs().clear();
    }

    @Test
    void testInfoLogging() {
        JaverLogger.info("Test info message");

        assertFalse(testHandler.getLogs().isEmpty());

        LogRecord record = testHandler.getLogs().get(0);
        assertEquals(Level.INFO, record.getLevel());
        assertEquals("Test info message", record.getMessage());
    }

    @Test
    void testErrorLogging() {
        JaverLogger.error("Test error");

        LogRecord record = testHandler.getLogs().get(0);
        assertEquals(Level.SEVERE, record.getLevel());
        assertEquals("Test error", record.getMessage());
    }

    @Test
    void testMultipleLogs() {
        JaverLogger.info("One");
        JaverLogger.warning("Two");

        assertEquals(2, testHandler.getLogs().size());
    }
}