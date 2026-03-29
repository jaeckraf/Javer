package ch.zhaw.it.pm4.javer.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
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
    void newDiagnosticBag_HasNoErrors() {
        assertFalse(diagnostics.hasErrors(), "A fresh diagnostic bag should not contain any errors.");
        assertTrue(diagnostics.getErrors().isEmpty(), "The error list should be empty upon initialization.");
    }

    @Test
    void reportError_GeneralMessage_AddsErrorToBag() {
        String errorMessage = "An input file is required.";

        diagnostics.reportError(errorMessage);

        assertTrue(diagnostics.hasErrors(), "Bag should register an error after one is reported.");
        assertEquals(1, diagnostics.getErrors().size(), "There should be exactly one error in the list.");
        assertEquals(errorMessage, diagnostics.getErrors().getFirst(), "The error message should match exactly.");
    }

    @Test
    void reportError_MultipleGeneralMessages_AddsAllErrorsInOrder() {
        diagnostics.reportError("First error");
        diagnostics.reportError("Second error");

        assertTrue(diagnostics.hasErrors());
        List<String> errors = diagnostics.getErrors();

        assertEquals(2, errors.size(), "There should be exactly two errors.");
        assertEquals("First error", errors.get(0));
        assertEquals("Second error", errors.get(1));
    }

    @Test
    void reportError_WithNullToken_UsesUnknownFallback() {
        String errorMessage = "Unexpected symbol.";

        diagnostics.reportError(null, errorMessage);

        assertTrue(diagnostics.hasErrors());
        String reportedError = diagnostics.getErrors().getFirst();

        assertTrue(reportedError.contains("[UNKNOWN]"), "Should use [UNKNOWN] fallback for null tokens.");
        assertTrue(reportedError.contains(errorMessage), "Should contain the actual error message.");
        assertEquals("Error at token [UNKNOWN]: Unexpected symbol.", reportedError);
    }

    @Test
    void reportError_WithValidToken_FormatsMessageCorrectly() {
        Token dummyToken = new Token(TokenType.EOF, "\n");

        String errorMessage = "Unexpected EOF.";

        diagnostics.reportError(dummyToken, errorMessage);

        assertTrue(diagnostics.hasErrors(), "Bag should register the error.");
        String reportedError = diagnostics.getErrors().getFirst();

        assertTrue(reportedError.contains("Error at token ["), "Should format the message with the token type.");
        assertTrue(reportedError.contains(errorMessage), "Should contain the actual error message.");
    }
}