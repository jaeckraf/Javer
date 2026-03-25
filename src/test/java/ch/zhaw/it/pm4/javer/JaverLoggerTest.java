package ch.zhaw.it.pm4.javer;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

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