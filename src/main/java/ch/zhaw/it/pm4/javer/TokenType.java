package ch.zhaw.it.pm4.javer;

/**
 * Enum representing different types of tokens.
 */
public enum TokenType {
    // Literals
    INTEGER_LITERAL,
    DOUBLE_LITERAL,
    HEX_LITERAL,
    BINARY_LITERAL,
    OCTAL_LITERAL,
    STRING_LITERAL,
    CHAR_LITERAL,
    BOOLEAN_LITERAL,
    NULL_LITERAL,
    IDENTIFIER,

    
    // Keywords
    IF,
    ELSE,
    WHILE,
    DO,
    FOR,
    RETURN,
    FUNCTION,
    BREAK,
    CONTINUE,
    SWITCH,
    CASE,
    DEFAULT,
    LET,
    CALL,
    TRUE,
    FALSE,
    NULL,

    //Types and Return types
    STRUCT,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    STRING,
    CHARACTER,
    VOID,
    ENUM,


    // Operators
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    ASSIGN,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    MULTIPLY_ASSIGN,
    DIVIDE_ASSIGN,
    MODULO_ASSIGN,
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL,
    GREATER_EQUAL,
    LOGICAL_NOT,
    NOT,
    INCREMENT,
    DECREMENT,
    OR,
    AND,
    BITWISE_AND,
    BITWISE_OR,
    BITWISE_XOR,
    BITWISE_NOT,
    BITSHIFT_LEFT,
    BITSHIFT_RIGHT,
    BITWISE_OR_ASSIGN,
    BITWISE_AND_ASSIGN,
    BITWISE_XOR_ASSIGN,
    BITSHIFT_LEFT_ASSIGN,
    BITSHIFT_RIGHT_ASSIGN,
    
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
    COLON,
    QUESTION_MARK,
    
    // SpecialW
    END_OF_FILE,         // End of file
    UNKNOWN

}
