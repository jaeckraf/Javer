package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

/**
 * Represents a single user-facing issue discovered during compilation.
 *
 * <p>Examples:</p>
 *
 * <ul>
 * <li>invalid character (lexer)</li>
 * <li>unexpected token (parser)</li>
 * <li>unknown identifier (name resolution)</li>
 * <li>type mismatch (type check)</li>
 * </ul>
 */
public class Diagnostic {

    private final SourceLocation location;
    private final Severity severity;
    private final String message;

    /**
     * Creates a new Diagnostic with an initial message.
     *
     * @param location The precise location of the issue in the source code.
     * @param severity The severity level of the issue.
     * @param message The message.
     */
    public Diagnostic(SourceLocation location, Severity severity, String message) {
        this.location = location;
        this.severity = severity;
        this.message = message;
    }

    /**
     * @return source location associated with the diagnostic
     */
    public SourceLocation getLocation() {
        return location;
    }

    /**
     * @return diagnostic severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * @return user-facing diagnostic message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] at %s: %s", severity.name(), location == null ? "<unknown>" : location.toString(), message);
    }
}
