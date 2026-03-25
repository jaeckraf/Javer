package ch.zhaw.it.pm4.javer;

import java.util.Objects;

/**
 * Immutable class representing the position of a token in the source code.
 * Contains the start column, end column, and line number for precise error reporting.
 */
public class TokenPosition {
    private final int startColumn;
    private final int endColumn;
    private final int lineNumber;

    /**
     * Creates a new TokenPosition with the specified start column, end column, and line number.
     *
     * @param startColumn the starting column of the token (must be >= 0)
     * @param endColumn the ending column of the token (must be >= startColumn)
     * @param lineNumber the line number of the token (must be >= 1)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public TokenPosition(int startColumn, int endColumn, int lineNumber) {
        if (startColumn < 0) {
            throw new IllegalArgumentException("Start column cannot be negative: " + startColumn);
        }
        if (endColumn < startColumn) {
            throw new IllegalArgumentException("End column cannot be less than start column: " + endColumn + " < " + startColumn);
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must be at least 1: " + lineNumber);
        }
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the starting column of the token.
     *
     * @return the start column
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Gets the ending column of the token.
     *
     * @return the end column
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Gets the line number of the token.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return String.format("TokenPosition{startColumn=%d, endColumn=%d, lineNumber=%d}", startColumn, endColumn, lineNumber);
    }
}
