package ch.zhaw.it.pm4.javer;

/**
 * Enum representing different types of tokens.
 */
public enum TokenType {
    // Literals
    NUMBER,
    STRING,
    IDENTIFIER,
    
    // Keywords
    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    
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
    
    // Delimiters
    LPAREN,      // (
    RPAREN,      // )
    LBRACE,      // {
    RBRACE,      // }
    LBRACKET,    // [
    RBRACKET,    // ]
    SEMICOLON,
    COMMA,
    DOT,
    
    // Special
    EOF,         // End of file
    UNKNOWN
}

