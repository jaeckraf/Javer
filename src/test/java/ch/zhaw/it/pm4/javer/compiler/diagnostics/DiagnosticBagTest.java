package ch.zhaw.it.pm4.javer.compiler.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenPosition;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticBagTest {

    private DiagnosticBag diagnostics;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticBag();
    }

    @Test
    void reportError_StoresFormattedMessage() {
        Token token = new Token(TokenType.KEYWORD_LET, "let", new TokenPosition(1, 3, 1));
        diagnostics.reportError(token, "Unexpected token");

        List<String> errors = diagnostics.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("KEYWORD_LET"));
        assertTrue(errors.get(0).contains("Unexpected token"));
    }

    @Test
    void hasErrors_ReturnsCorrectStatus() {
        assertFalse(diagnostics.hasErrors());
        
        diagnostics.reportError(new Token(TokenType.ID_IDENTIFIER, "x", new TokenPosition(1, 1, 1)), "Error");
        assertTrue(diagnostics.hasErrors());
    }

    @Test
    void getErrors_ReturnsUnmodifiableList() {
        diagnostics.reportError(new Token(TokenType.ID_IDENTIFIER, "x", new TokenPosition(1, 1, 1)), "Error");
        List<String> errors = diagnostics.getErrors();
        
        assertThrows(UnsupportedOperationException.class, () -> errors.add("New Error"));
    }

    @Test
    void clear_ResetsErrorList() {
        diagnostics.reportError(new Token(TokenType.ID_IDENTIFIER, "x", new TokenPosition(1, 1, 1)), "Error");
        assertTrue(diagnostics.hasErrors());
        
        diagnostics.clear();
        assertFalse(diagnostics.hasErrors());
        assertEquals(0, diagnostics.getErrors().size());
    }
}
