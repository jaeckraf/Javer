package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

/**
 * Represents a single user-facing issue discovered during compilation.
 * * Examples:
 * - invalid character (lexer)
 * - unexpected token (parser)
 * - unknown identifier (name resolution)
 * - type mismatch (type check)
 */
public class Diagnostic {

    private final SourceLocation location;
    private final Severity severity;
    private final String message;

    /**
     * Creates a new Diagnostic with an initial message.
     * * @param location The precise location of the issue in the source code.
     * @param severity The severity level of the issue.
     * @param message The message.
     */
    public Diagnostic(SourceLocation location, Severity severity, String message) {
        this.location = location;
        this.severity = severity;
        this.message = message;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        // Formats the diagnostic nicely: [ERROR] at [1 : 5 : 2]: Type mismatch. Expected int.
        return String.format("[%s] at %s: %s", severity.name(), location.toString(), message);
    }
}
