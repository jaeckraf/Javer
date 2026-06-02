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
public record Diagnostic(SourceLocation location, Severity severity, String message) {

    /**
     * Creates a new Diagnostic with an initial message.
     *
     * @param location The precise location of the issue in the source code.
     * @param severity The severity level of the issue.
     * @param message  The message.
     */
    public Diagnostic {
    }

    /**
     * @return source location associated with the diagnostic
     */
    @Override
    public SourceLocation location() {
        return location;
    }

    /**
     * @return diagnostic severity
     */
    @Override
    public Severity severity() {
        return severity;
    }

    /**
     * @return user-facing diagnostic message
     */
    @Override
    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] at %s: %s", severity.name(), location.toString(), message);
    }
}
