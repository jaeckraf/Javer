package ch.zhaw.it.pm4.javer.compiler.misc;

import ch.zhaw.it.pm4.javer.JaverLogger;
import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;

/**
 * Immutable record representing a location inside the source file.
 * Contains the start column, end column, and line number for precise error reporting.
 * * Used by:
 * - Tokens
 * - AST nodes
 * - Diagnostics
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public record SourceLocation(int startColumn, int endColumn, int lineNumber) {

    /**
     * Compact constructor to validate the source location boundaries.
     * Enforces that locations map to real, physical text coordinates (1-indexed).
     *
     */
    public SourceLocation {
        if (startColumn < 1) {
            JaverLogger.error("Start column must be at least 1: " + startColumn);
        }
        if (endColumn < startColumn) {
            JaverLogger.error("End column cannot be less than start column: " + endColumn + " < " + startColumn);
        }
        if (lineNumber < 1) {
            JaverLogger.error("Line number must be at least 1: " + lineNumber);
        }
    }

    /**
     * Formats the location for debugging and error reporting.
     * Output format: [startColumn : endColumn : lineNumber]
     */
    @Override
    public String toString() {
        return String.format("[%d : %d : %d]", startColumn, endColumn, lineNumber);
    }
}
