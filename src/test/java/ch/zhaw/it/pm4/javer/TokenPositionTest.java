package ch.zhaw.it.pm4.javer;

import ch.zhaw.it.pm4.javer.compiler.lexer.TokenPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the TokenPosition record.
 * Tests validation of constructor parameters and basic functionality.
 */
class TokenPositionTest {

    @Test
    @DisplayName("Should create TokenPosition with valid values")
    void testValidTokenPosition() {
        TokenPosition pos = new TokenPosition(1, 5, 1);
        assertEquals(1, pos.startColumn());
        assertEquals(5, pos.endColumn());
        assertEquals(1, pos.lineNumber());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when startColumn is less than 1")
    void testStartColumnLessThanOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TokenPosition(0, 5, 1);
        });
        assertTrue(exception.getMessage().contains("Start column must be at least 1"));
        assertTrue(exception.getMessage().contains("0"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when endColumn is less than startColumn")
    void testEndColumnLessThanStart() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TokenPosition(5, 3, 1);
        });
        assertTrue(exception.getMessage().contains("End column cannot be less than start column"));
        assertTrue(exception.getMessage().contains("3 < 5"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when lineNumber is less than 1")
    void testLineNumberLessThanOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TokenPosition(1, 5, 0);
        });
        assertTrue(exception.getMessage().contains("Line number must be at least 1"));
        assertTrue(exception.getMessage().contains("0"));
    }

    @Test
    @DisplayName("Should allow startColumn and endColumn to be equal")
    void testStartAndEndEqual() {
        assertDoesNotThrow(() -> {
            new TokenPosition(5, 5, 1);
        });
    }

    @Test
    @DisplayName("Should allow minimum valid values")
    void testMinimumValidValues() {
        assertDoesNotThrow(() -> {
            new TokenPosition(1, 1, 1);
        });
    }

    @Test
    @DisplayName("Should have correct toString output")
    void testToString() {
        TokenPosition pos = new TokenPosition(1, 10, 2);
        assertEquals("[1 : 10 : 2]", pos.toString());
    }
}
