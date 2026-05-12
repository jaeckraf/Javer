package ch.zhaw.it.pm4.javer.compiler.lexer;

/**
 * Enum representing different types of tokens.
 */
public enum TokenType {
    // Literals
    LITERAL_INTEGER("Literal: integer"),
    LITERAL_DOUBLE("Literal: double"),
    LITERAL_HEX("Literal: hex"),
    LITERAL_BINARY("Literal: binary"),
    LITERAL_OCTAL("Literal: octal"),
    LITERAL_STRING("Literal: string"),
    LITERAL_CHAR("Literal: char"),
    LITERAL_BOOLEAN("Literal: boolean"),
    LITERAL_NULL("null"),

    // Identifiers
    ID_IDENTIFIER("Identifier"),

    
    // Keywords
    KEYWORD_IF("if"),
    KEYWORD_ELSE("else"),
    KEYWORD_WHILE("while"),
    KEYWORD_DO("do"),
    KEYWORD_FOR("for"),
    KEYWORD_RETURN("return"),
    KEYWORD_FUNCTION("fn"),
    KEYWORD_BREAK("break"),
    KEYWORD_CONTINUE("continue"),
    KEYWORD_SWITCH("switch"),
    KEYWORD_CASE("case"),
    KEYWORD_DEFAULT("default"),
    KEYWORD_LET("let"),
    KEYWORD_CALL("call"),
    KEYWORD_NEW("new"),

    //Types and Return types
    TYPE_STRUCT("struct"),
    TYPE_INTEGER("int"),
    TYPE_DOUBLE("double"),
    TYPE_BOOLEAN("boolean"),
    TYPE_STRING("string"),
    TYPE_CHARACTER("char"),
    TYPE_VOID("void"),
    TYPE_ENUM("enum"),


    // Operators
    OPERATOR_PLUS("+"),
    OPERATOR_MINUS("-"),
    OPERATOR_MULTIPLY("*"),
    OPERATOR_DIVIDE("/"),
    OPERATOR_MODULO("%"),
    OPERATOR_ASSIGN("="),
    OPERATOR_PLUS_ASSIGN("+="),
    OPERATOR_MINUS_ASSIGN("-="),
    OPERATOR_MULTIPLY_ASSIGN("*="),
    OPERATOR_DIVIDE_ASSIGN("/="),
    OPERATOR_MODULO_ASSIGN("%="),
    OPERATOR_EQUALS("=="),
    OPERATOR_NOT_EQUALS("!="),
    OPERATOR_LESS_THAN("<"),
    OPERATOR_GREATER_THAN(">"),
    OPERATOR_LESS_EQUAL("<="),
    OPERATOR_GREATER_EQUAL(">="),
    OPERATOR_LOGICAL_NOT("!"),
    OPERATOR_INCREMENT("++"),
    OPERATOR_DECREMENT("--"),
    OPERATOR_OR("||"),
    OPERATOR_AND("&&"),
    OPERATOR_BITWISE_AND("&"),
    OPERATOR_BITWISE_OR("|"),
    OPERATOR_BITWISE_XOR("^"),
    OPERATOR_BITWISE_NOT("~"),
    OPERATOR_BITSHIFT_LEFT("<<"),
    OPERATOR_BITSHIFT_RIGHT(">>"),
    OPERATOR_BITWISE_OR_ASSIGN("|="),
    OPERATOR_BITWISE_AND_ASSIGN("&="),
    OPERATOR_BITWISE_XOR_ASSIGN("^="),
    OPERATOR_BITSHIFT_LEFT_ASSIGN("<<="),
    OPERATOR_BITSHIFT_RIGHT_ASSIGN(">>="),
    
    // Delimiters
    SYMBOL_LEFT_PARENTHESIS("("),
    SYMBOL_RIGHT_PARENTHESIS(")"),
    SYMBOL_LEFT_BRACE("{"),
    SYMBOL_RIGHT_BRACE("}"),
    SYMBOL_LEFT_BRACKET("["),
    SYMBOL_RIGHT_BRACKET("]"),
    SYMBOL_SEMICOLON(";"),
    SYMBOL_COMMA(","),
    SYMBOL_DOT("."),
    SYMBOL_COLON(":"),
    SYMBOL_QUESTION_MARK("?"),
    
    // Special Tokens
    SPECIAL_END_OF_FILE("end of file"),
    SPECIAL_UNKNOWN("unknown token");

    private final String diagnosticName;

    TokenType(String diagnosticName) {
        this.diagnosticName = diagnosticName;
    }

    public String diagnosticName() {
        return diagnosticName;
    }

}
