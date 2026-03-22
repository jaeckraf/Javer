package ch.zhaw.it.pm4.javer;

import java.util.Objects;

/**
 * Immutable class representing a token extracted from source code.
 * A token consists of a type, its string value, and its position in the source.
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int position;
    
    /**
     * Creates a new Token with the specified type, value, and position.
     * 
     * @param type the type of the token (must not be null)
     * @param value the string value of the token as read from the file
     * @param position the position of the token in the source (must be >= 0)
     * @throws NullPointerException if type is null
     * @throws IllegalArgumentException if position is negative
     */
    public Token(TokenType type, String value, int position) {
        this.type = Objects.requireNonNull(type, "Token type cannot be null");
        if (position < 0) {
            throw new IllegalArgumentException("Token position cannot be negative: " + position);
        }
        this.value = value;
        this.position = position;
    }
    
    /**
     * Gets the type of this token.
     * 
     * @return the TokenType of this token
     */
    public TokenType getType() {
        return type;
    }
    
    /**
     * Gets the string value of this token as read from the file.
     * 
     * @return the token's value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Gets the position of this token in the source code.
     * This is useful for error reporting and diagnostics.
     * 
     * @return the position of the token
     */
    public int getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s', position=%d}", type, value, position);
    }
}

