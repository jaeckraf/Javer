package ch.zhaw.it.pm4.javer.compiler.lexer;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
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
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Token(null, "value", new SourceLocation(1, 2, 1)));
        assertEquals("Token type cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw NullPointerException with correct message when position is null")
    void testTokenPositionCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, "value", null));
        assertEquals("Token position cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should accept position with valid values")
    void testTokenPositionValid() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, "", new SourceLocation(1, 2, 1));
        });
    }
    
    @Test
    @DisplayName("Should accept null value (edge case)")
    void testTokenValueCanBeNull() {
        assertDoesNotThrow(() -> {
            new Token(TokenType.SYMBOL_LEFT_PARENTHESIS, null, new SourceLocation(1, 2, 1));
        });
    }

    @Test
    @DisplayName("Should format token dump line with aligned fields")
    void testTokenDumpFormat() {
        Token token = new Token(TokenType.KEYWORD_FUNCTION, "fn", new SourceLocation(1, 2, 1));

        assertEquals(String.format(
                "TokenType: %35s, value: '%15s', position: [%3d : %3d : %3d ]",
                TokenType.KEYWORD_FUNCTION,
                "fn",
                1,
                1,
                2
        ), token.toString());
    }
}
