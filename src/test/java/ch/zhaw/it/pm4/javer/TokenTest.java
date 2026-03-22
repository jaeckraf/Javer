package ch.zhaw.it.pm4.javer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Negative tests for the Token class.
 * Tests validation of constructor parameters.
 */
class TokenTest {
    
    @Test
    @DisplayName("Should throw NullPointerException with correct message when type is null")
    void testTokenTypeCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new Token(null, "value", 0);
        });
        assertEquals("Token type cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when position is negative")
    void testTokenPositionCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Token(TokenType.NUMBER, "123", -1);
        });
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when position is negative")
    void testTokenPositionNegativeExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Token(TokenType.IDENTIFIER, "foo", -5);
        });
        assertTrue(exception.getMessage().contains("Token position cannot be negative"));
        assertTrue(exception.getMessage().contains("-5"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when position is -1")
    void testTokenPositionMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Token(TokenType.STRING, "hello", -1);
        });
    }
    
    @Test
    @DisplayName("Should accept position 0 (minimum valid value)")
    void testTokenPositionZeroIsValid() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.END_OF_FILE, "", 0);
        });
    }
    
    @Test
    @DisplayName("Should accept null value (edge case)")
    void testTokenValueCanBeNull() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.END_OF_FILE, null, 0);
        });
    }
}


