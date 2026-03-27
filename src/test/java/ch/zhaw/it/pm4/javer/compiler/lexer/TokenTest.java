package ch.zhaw.it.pm4.javer.compiler.lexer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
            new Token(null, "value", new TokenPosition(1, 2, 1));
        });
        assertEquals("Token type cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw NullPointerException with correct message when position is null")
    void testTokenPositionCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, "value", null);
        });
        assertEquals("Token position cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should accept position with valid values")
    void testTokenPositionValid() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, "", new TokenPosition(1, 2, 1));
        });
    }
    
    @Test
    @DisplayName("Should accept null value (edge case)")
    void testTokenValueCanBeNull() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, null, new TokenPosition(1, 2, 1));
        });
    }
}
