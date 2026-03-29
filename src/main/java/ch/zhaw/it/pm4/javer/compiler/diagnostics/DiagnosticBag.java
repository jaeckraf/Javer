package ch.zhaw.it.pm4.javer.compiler.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects and manages errors and warnings generated during the various phases
 * of the compilation process (Argument Parsing, Lexing, Parsing, etc.).
 * <p>
 * Instead of throwing exceptions and crashing the compiler on the first mistake,
 * the DiagnosticBag collects all issues. This allows the compiler to provide
 * comprehensive, constructive feedback to the user, aligning with Javer's goal
 * of being beginner-friendly.
 */
public class DiagnosticBag {

    /**
     * Internal list storing the formatted error messages.
     * In future sprints, this might be upgraded to a list of custom Diagnostic objects
     * to hold more detailed metadata (like line numbers and columns).
     */
    private final List<String> errors = new ArrayList<>();

    /**
     * Reports an error that is tied to a specific token in the source code.
     * This is primarily used by the Lexer and Parser to point exactly to where
     * a syntax or semantic error occurred.
     *
     * @param token        The token where the error occurred (can be null for EOF issues).
     * @param errorMessage The constructive error message to display to the user.
     */
    public void reportError(Token token, String errorMessage) {
        String tokenType = (token != null) ? token.getType().toString() : "UNKNOWN";
        errors.add("Error at token [" + tokenType + "]: " + errorMessage);
    }

    /**
     * Reports a general, system-level error that is not tied to a specific
     * token in the source code.
     * <p>
     * This is primarily used for command-line argument validation or file I/O errors.
     *
     * @param errorMessage The constructive error message to display to the user.
     */
    public void reportError(String errorMessage) {
        errors.add(errorMessage);
    }

    /**
     * Checks if any errors have been reported during the compilation process.
     *
     * @return true if the bag contains one or more errors; false otherwise.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Retrieves the complete list of collected errors.
     *
     * @return A list of error message strings.
     */
    public List<String> getErrors() {
        return errors;
    }
}