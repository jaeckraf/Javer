package ch.zhaw.it.pm4.javer;

import java.util.Objects;

/**
 * Immutable class representing a token extracted from source code.
 * A token consists of a type, its string value, and its position in the source.
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final TokenPosition position;
    
    /**
     * Creates a new Token with the specified type, value, and position.
     * 
     * @param type the type of the token (must not be null)
     * @param value the string value of the token as read from the file
     * @param position the position of the token in the source (must not be null)
     * @throws NullPointerException if type or position is null
     */
    public Token(TokenType type, String value, TokenPosition position) {
        this.type = Objects.requireNonNull(type, "Token type cannot be null");
        this.position = Objects.requireNonNull(position, "Token position cannot be null");
        this.value = value;
    }
    
    /**
     * Gets the type of this token.
     * 
     * @return the TokenType of this token
     */
    public TokenType getTokenType() {
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
    public TokenPosition getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s', position=%s}", type, value, position);
    }
}
