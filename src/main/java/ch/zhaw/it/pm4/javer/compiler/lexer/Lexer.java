package ch.zhaw.it.pm4.javer.compiler.lexer;

import ch.zhaw.it.pm4.javer.compiler.CompilationContext;
import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Lexer class is responsible for converting the raw source code into a sequence of tokens that can be used by the parser.
 * It reads the source code character by character, identifies valid tokens (like keywords, identifiers, literals, and symbols), and handles any lexical errors by reporting them through the DiagnosticBag.
 * The lexer also keeps track of the current position in the source code (line and column) to provide accurate error messages and token positions.
 * */

public class Lexer {
    private final String sourceCode;
    private int indexInSourceCode = 0;
    private int line;
    private int startColumn;
    private int endColumn;

    private static final Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("if", TokenType.KEYWORD_IF),
            Map.entry("else", TokenType.KEYWORD_ELSE),
            Map.entry("while", TokenType.KEYWORD_WHILE),
            Map.entry("do", TokenType.KEYWORD_DO),
            Map.entry("for", TokenType.KEYWORD_FOR),
            Map.entry("return", TokenType.KEYWORD_RETURN),
            Map.entry("function", TokenType.KEYWORD_FUNCTION),
            Map.entry("break", TokenType.KEYWORD_BREAK),
            Map.entry("continue", TokenType.KEYWORD_CONTINUE),
            Map.entry("switch", TokenType.KEYWORD_SWITCH),
            Map.entry("case", TokenType.KEYWORD_CASE),
            Map.entry("default", TokenType.KEYWORD_DEFAULT),
            Map.entry("let", TokenType.KEYWORD_LET),
            Map.entry("call", TokenType.KEYWORD_CALL),
            // Types
            Map.entry("struct", TokenType.TYPE_STRUCT),
            Map.entry("int", TokenType.TYPE_INTEGER),
            Map.entry("double", TokenType.TYPE_DOUBLE),
            Map.entry("boolean", TokenType.TYPE_BOOLEAN),
            Map.entry("string", TokenType.TYPE_STRING),
            Map.entry("char", TokenType.TYPE_CHARACTER),
            Map.entry("void", TokenType.TYPE_VOID),
            Map.entry("enum", TokenType.TYPE_ENUM)
    );

    /**
     * @param sourceCode the raw source code to be tokenized
     * */
    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Lexes the entire source code and returns a list of tokens. This method will repeatedly call nextToken() until the end of the source code is reached, collecting all tokens into a list.
     * If any lexical errors are encountered during tokenization, they will be reported through the DiagnosticBag.
     * */
    public List<Token> lexSourcecode() {
        return new ArrayList<>();
    }

    /**
     * This method will read characters from the source code, identify the type of token (e.g., keyword, identifier, literal, symbol), and return a Token object representing it.
     * @return the next token from the source code.
     * */
    private Token nextToken() {
        return null;
    }

    /**
     * @param tokenType the type of token to create (e.g., keyword, identifier, literal, symbol)
     * @return returns a new Token object with the specified type and the current position in the source code.
     * */
    private Token makeToken(TokenType tokenType) { return null; }
    /**
     * @return returns a new TokenPosition object representing the current position in the source code, using the start column, end column, and line number.
     * This is used for accurate error reporting and token metadata.
     * */
    private SourceLocation makeSourceLocation() {
        return new SourceLocation(startColumn, startColumn + endColumn, line);
    }

    /**
     * @param message the error message to report
     * This method will report a lexical error with the given message, including the current position in the source code for accurate diagnostics.
     * The error will be added to the DiagnosticBag for later retrieval and reporting to the user.
     * */
    private void error(String message) {

    }

    /**
     * This method skips whitespace characters (like spaces, tabs, and newlines) and comments in the source code. It will advance the current position until it encounters a non-whitespace, non-comment character or reaches the end of the source code.
     * This ensures that the lexer only processes meaningful tokens and ignores irrelevant characters.
     * The method should also handle different types of comments (e.g., single-line comments starting with // and multi-line comments enclosed in slash-star and star-slash) and report any unterminated comment errors if necessary.
     * */
    private void skipWhitespaceAndComments() {  }

    /**
     * @return returns a Token object representing the next token in the source code, which is a number literal.
     * */
    private Token lexNumber() { return null; }

    /**
     * @return returns a Token object representing the next token in the source code, which is a string literal.
     * */
    private Token lexString() { return null; }

    /**
     * @return returns a Token object representing the next token in the source code, which is a char literal.
     * */
    private Token lexChar() { return null; }

    /**
     * @return returns a Token object representing the next token in the source code, which is a symbol literal.
     * */
    private Token lexSymbol() { return null; }

    /**
     * @return returns a Token object representing the next token in the source code, which is a keyword or an identifier.
     * The method will read characters until it encounters a non-identifier character, then check if the resulting string matches any known keywords.
     * If it does, it will return a keyword token; otherwise, it will return an identifier token.
     * */
    private Token lexIdentifierOrKeyword() { return null; }

    /**
     * @return returns the current character in the source code at the current index. If the index is out of bounds (i.e., past the end of the source code), it returns a null character ('\0') to indicate the end of input.
     * This method is used to safely access characters in the source code without risking an IndexOutOfBoundsException, and it allows the lexer to detect when it has reached the end of the source code.
     * */
    private char currentChar() {
        return indexInSourceCode < sourceCode.length() ? sourceCode.charAt(indexInSourceCode) : '\0';
    }

    /**
     * @return returns the character at the specified offset from the current index in the source code. If the resulting index is out of bounds, it returns a null character ('\0').
     * This method allows the lexer to look ahead in the source code without advancing the current position, which is useful for making decisions based on upcoming characters (e.g., distinguishing between '=' and '==').
     * */
    private char peek(int offset) {
        return indexInSourceCode + offset < sourceCode.length() ? sourceCode.charAt(indexInSourceCode + offset) : '\0';
    }

    /**
     * This method advances the current index in the source code by one character, updating the line and column counters accordingly. If the current character is a newline ('\n'), it increments the line number and resets the column to zero; otherwise, it increments the column number.
     * This method is essential for moving through the source code while keeping accurate track of the current position for error reporting and token metadata.
     * */
    private void advance() {
        if(indexInSourceCode < sourceCode.length()) {
            indexInSourceCode++;
            if(currentChar() == '\n') {
                line++;
                startColumn = 0;
            } else {
                startColumn++;
            }
            indexInSourceCode++;
        }
    }

    /**
     * @return returns true if the given character is a valid starting character for an identifier (i.e., a letter or an underscore), and false otherwise. This method is used to determine if the lexer should start lexing an identifier or keyword when it encounters a character.
     * In many programming languages, identifiers must start with a letter (a-z, A-Z) or an underscore (_), and cannot start with a digit or other special characters. This method enforces that rule during tokenization.
     * */
    private boolean isIdentifierStart(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    /**
     * @return returns true if the given character is a valid starting character for a keyword (i.e., a letter), and false otherwise. This method is used to determine if the lexer should start lexing a keyword when it encounters a character.
     * In many programming languages, keywords must start with a letter (a-z, A-Z) and cannot start with an underscore or digit. This method enforces that rule during tokenization.
     * */
    private boolean isKeywordStart(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    /**
     * @return returns true if the given character is a valid hexadecimal digit (i.e., 0-9, a-f, A-F), and false otherwise. This method is used to determine if a character can be part of a hexadecimal number literal during tokenization.
     * Hexadecimal digits include the numbers 0 through 9 and the letters A through F (both uppercase and lowercase), which represent the values 10 through 15. This method checks for those valid characters when lexing hexadecimal literals.
     * For example, in a hexadecimal literal like "0x1A3F", the characters '1', 'A', '3', and 'F' would all return true when passed to this method.
     * */
    private boolean isHexDigit(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    /**
     * @return returns true if the given character is a valid decimal digit (i.e., 0-9), and false otherwise. This method is used to determine if a character can be part of a decimal number literal during tokenization.
     * Decimal digits include the numbers 0 through 9. This method checks for those valid characters when lexing decimal literals.
     * For example, in a decimal literal like "12345", the characters '1', '2', '3', '4', and '5' would all return true when passed to this method.
     * */
    private boolean isDecimalDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * @return returns true if the given character is a valid octal digit (i.e., 0-7), and false otherwise. This method is used to determine if a character can be part of an octal number literal during tokenization.
     * Octal digits include the numbers 0 through 7. This method checks for those valid characters when lexing octal literals.
     * For example, in an octal literal like "0o0755", the characters '0', '7', '5', and '5' would all return true when passed to this method, while '8' or '9' would return false.
     * */
    private boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }

    /**
     * @return returns true if the given character is a valid binary digit (i.e., 0 or 1), and false otherwise. This method is used to determine if a character can be part of a binary number literal during tokenization.
     * Binary digits include only the numbers 0 and 1. This method checks for those valid characters when lexing binary literals.
     * For example, in a binary literal like "0b1010", the characters '1' and '0' would return true when passed to this method, while any other character would return false.
     * */
    private boolean isBinaryDigit(char c) {
        return c == '0' || c == '1';
    }

    /**
     * @param c the character to check
     * @param base the base of the number system (only 2, 8, 10, 16)
     * @return returns true if the given character is a valid digit for the specified base (2, 8, 10, or 16), and false otherwise. This method is used to determine if a character can be part of a number literal in the given base during tokenization.
     * The method uses a switch statement to check the base and calls the appropriate helper method (isBinaryDigit, isOctalDigit, isDecimalDigit, or isHexDigit) to validate the character based on the specified base.
     * For example, if the base is 16, this method will return true for characters '0' through '9', 'a' through 'f', and 'A' through 'F', while it will return false for any other character.
     * */
    private boolean isDigitForBase(char c, int base) {
        return switch (base) {
            case 2 -> isBinaryDigit(c);
            case 8 -> isOctalDigit(c);
            case 10 -> isDecimalDigit(c);
            case 16 -> isHexDigit(c);
            default -> false;
        };
    }

}
