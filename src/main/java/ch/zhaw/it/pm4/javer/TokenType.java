package ch.zhaw.it.pm4.javer;

/**
 * Enum representing different types of tokens.
 */
public enum TokenType {
    // Literals
    NUMBER,
    IDENTIFIER,

    
    // Keywords
    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    ENUM,
    FUNCTION,
    BREAK,
    CONTINUE,
    SWITCH,
    CASE,
    DEFAULT,

    //Types and Return types
    STRUCT,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    STRING,
    CHARACTER,
    VOID,


    // Operators
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    ASSIGN,
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL,
    GREATER_EQUAL,
    OR,
    AND,
    NOT,
    
    // Delimiters
    LEFT_PARENTHESIS,      // (
    RIGHT_PARENTHESIS,      // )
    LEFT_BRACE,      // {
    RIGHT_BRACE,      // }
    LEFT_BRACKET,    // [
    RIGHT_BRACKET,    // ]
    SEMICOLON,
    COMMA,
    DOT,
    
    // SpecialW
    END_OF_FILE,         // End of file
    UNKNOWN

}
