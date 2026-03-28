package ch.zhaw.it.pm4.javer.compiler.lexer;

/**
 * Immutable class representing the position of a token in the source code.
 * Contains the start column, end column, and line number for precise error reporting.
 */
public record TokenPosition(int startColumn, int endColumn, int lineNumber) {
    /**
     * Creates a new TokenPosition with the specified start column, end column, and line number.
     *
     * @param startColumn the starting column of the token (must be >= 1)
     * @param endColumn   the ending column of the token (must be >= startColumn)
     * @param lineNumber  the line number of the token (must be >= 1)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public TokenPosition {
        if (startColumn < 1) {
            throw new IllegalArgumentException("Start column must be at least 1: " + startColumn);
        }
        if (endColumn < startColumn) {
            throw new IllegalArgumentException("End column cannot be less than start column: " + endColumn + " < " + startColumn);
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must be at least 1: " + lineNumber);
        }
    }

    /**
     * Gets the starting column of the token.
     *
     * @return the start column
     */
    @Override
    public int startColumn() {
        return startColumn;
    }

    /**
     * Gets the ending column of the token.
     *
     * @return the end column
     */
    @Override
    public int endColumn() {
        return endColumn;
    }

    /**
     * Gets the line number of the token.
     *
     * @return the line number
     */
    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return String.format("[%d : %d : %d]", startColumn, endColumn, lineNumber);
    }
}
