package ch.zhaw.it.pm4.javer.compiler.lexer;

import ch.zhaw.it.pm4.javer.JaverLogger;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Lexer class is responsible for converting the raw source code into a
 * sequence of tokens that can be used by the parser.
 * It reads the source code character by character, identifies valid tokens
 * (like keywords, identifiers, literals, and symbols), and handles any lexical
 * errors by reporting them through the DiagnosticBag.
 * The lexer also keeps track of the current position in the source code (line
 * and column) to provide accurate error messages and token positions.
 */

public class Lexer {
    private final String sourceCode;
    private final DiagnosticBag diagnostics;

    private int indexInSourceCode = 0;
    private int line = 1;
    private int column = 1;

    private int tokenStartIndex = 0;
    private int tokenStartLine = 1;
    private int tokenStartColumn = 1;

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
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
            Map.entry("enum", TokenType.TYPE_ENUM));

    /**
     * Single-character symbols that require no lookahead. Any character present
     * in this map is lexed by simply consuming it and emitting the mapped token
     * type. This keeps {@link #lexSymbol()} focused on the ambiguous cases
     * (e.g. {@code +} vs {@code ++} vs {@code +=}).
     */
    private static final Map<Character, TokenType> SINGLE_CHAR_SYMBOLS = Map.ofEntries(
            Map.entry('(', TokenType.SYMBOL_LEFT_PARENTHESIS),
            Map.entry(')', TokenType.SYMBOL_RIGHT_PARENTHESIS),
            Map.entry('{', TokenType.SYMBOL_LEFT_BRACE),
            Map.entry('}', TokenType.SYMBOL_RIGHT_BRACE),
            Map.entry('[', TokenType.SYMBOL_LEFT_BRACKET),
            Map.entry(']', TokenType.SYMBOL_RIGHT_BRACKET),
            Map.entry(';', TokenType.SYMBOL_SEMICOLON),
            Map.entry(',', TokenType.SYMBOL_COMMA),
            Map.entry('.', TokenType.SYMBOL_DOT),
            Map.entry(':', TokenType.SYMBOL_COLON),
            Map.entry('?', TokenType.SYMBOL_QUESTION_MARK),
            Map.entry('~', TokenType.OPERATOR_BITWISE_NOT));

    /**
     * @param sourceCode the raw source code to be tokenized
     */
    public Lexer(String sourceCode) {
        this(sourceCode, null);
    }

    /**
     * @param sourceCode  the raw source code to be tokenized
     * @param diagnostics the diagnostic bag to report lexical errors to (may be
     *                    null)
     */
    public Lexer(String sourceCode, DiagnosticBag diagnostics) {
        this.sourceCode = sourceCode == null ? "" : sourceCode;
        this.diagnostics = diagnostics;
    }

    /**
     * Lexes the entire source code and returns a list of tokens. This method will
     * repeatedly call nextToken() until the end of the source code is reached,
     * collecting all tokens into a list.
     * If any lexical errors are encountered during tokenization, they will be
     * reported through the DiagnosticBag.
     * The returned list always ends with a single SPECIAL_END_OF_FILE token, which
     * the parser relies on as a sentinel.
     */
    public List<Token> lexSourcecode() {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            Token token = nextToken();
            tokens.add(token);
            if (token.getTokenType() == TokenType.SPECIAL_END_OF_FILE) {
                return tokens;
            }
        }
    }

    /**
     * This method will read characters from the source code, identify the type of
     * token (e.g., keyword, identifier, literal, symbol), and return a Token object
     * representing it.
     * 
     * @return The next token from the source code.
     */
    private Token nextToken() {
        skipWhitespaceAndComments();

        // Snapshot the start of the next token AFTER skipping trivia.
        tokenStartIndex = indexInSourceCode;
        tokenStartLine = line;
        tokenStartColumn = column;

        if (indexInSourceCode >= sourceCode.length()) {
            return makeToken(TokenType.SPECIAL_END_OF_FILE);
        }

        char currentChar = currentChar();

        if (isIdentifierStart(currentChar)) {
            return lexIdentifierOrKeyword();
        }
        if (isDecimalDigit(currentChar)) {
            return lexNumber();
        }
        if (currentChar == '"') {
            return lexString();
        }
        if (currentChar == '\'') {
            return lexChar();
        }
        return lexSymbol();
    }

    /**
     * @param tokenType the type of token to create (e.g., keyword, identifier,
     *                  literal, symbol)
     * @return A new Token object with the specified type and the current position
     *         in the source code.
     */
    private Token makeToken(TokenType tokenType) {
        String value = sourceCode.substring(tokenStartIndex, indexInSourceCode);
        return new Token(tokenType, value, defineSourceLocation());
    }

    /**
     * @return A new SourceLocation object representing the current position in the
     *         source code, using the start column, end column, and line number.
     *         This is used for accurate error reporting and token metadata.
     */
    private SourceLocation defineSourceLocation() {
        int safeStart = Math.max(1, tokenStartColumn);
        int endCol;
        if (line == tokenStartLine) {
            endCol = Math.max(safeStart, column - 1);
        } else {
            // Multi-line token (e.g. block string): SourceLocation is single-line, so
            // collapse to the token's start line.
            endCol = safeStart;
        }
        int safeLine = Math.max(1, tokenStartLine);
        return new SourceLocation(safeStart, endCol, safeLine);
    }

    /**
     * @param message the error message to report
     *                This method will report a lexical error with the given
     *                message, including the current position in the source code for
     *                accurate diagnostics.
     *                The error will be added to the DiagnosticBag for later
     *                retrieval and reporting to the user.
     */
    private void error(String message) {
        SourceLocation location = defineSourceLocation();
        if (diagnostics != null) {
            diagnostics.add(location, Severity.ERROR, message);
        } else {
            JaverLogger.error("Lexer error at " + location + ": " + message);
        }
    }

    /**
     * This method skips whitespace characters (like spaces, tabs, and newlines) and
     * comments in the source code. It will advance the current position until it
     * encounters a non-whitespace, non-comment character or reaches the end of the
     * source code.
     * This ensures that the lexer only processes meaningful tokens and ignores
     * irrelevant characters.
     * The method should also handle different types of comments (e.g., single-line
     * comments starting with // and multi-line comments enclosed in slash-star and
     * star-slash) and report any unterminated comment errors if necessary.
     */
    private void skipWhitespaceAndComments() {
        while (indexInSourceCode < sourceCode.length()) {
            char currentChar = currentChar();
            if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r' || currentChar == '\n') {
                advance();
                continue;
            }
            if (currentChar == '/' && peek(1) == '/') {
                while (indexInSourceCode < sourceCode.length() && currentChar() != '\n') {
                    advance();
                }
                continue;
            }
            if (currentChar == '/' && peek(1) == '*') {
                // Remember start of the block comment so error reporting points here.
                tokenStartIndex = indexInSourceCode;
                tokenStartLine = line;
                tokenStartColumn = column;
                advance();
                advance();
                boolean closed = false;
                while (indexInSourceCode < sourceCode.length()) {
                    if (currentChar() == '*' && peek(1) == '/') {
                        advance();
                        advance();
                        closed = true;
                        break;
                    }
                    advance();
                }
                if (!closed) {
                    error("Unterminated block comment");
                }
                continue;
            }
            break;
        }
    }

    /**
     * @return A Token object representing the next token in the source code, which
     *         is a number literal.
     *         Supports decimal integers/doubles (with optional exponent), hex
     *         (0x..), octal (0o..) and binary (0b..) literals.
     */
    private Token lexNumber() {
        if (currentChar() == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
            advance();
            advance();
            if (!isHexDigit(currentChar())) {
                error("Hexadecimal literal must have at least one digit");
            }
            consumeDigitsForBase(16);
            return makeToken(TokenType.LITERAL_HEX);
        }
        if (currentChar() == '0' && (peek(1) == 'o' || peek(1) == 'O')) {
            advance();
            advance();
            if (!isOctalDigit(currentChar())) {
                error("Octal literal must have at least one digit");
            }
            consumeDigitsForBase(8);
            return makeToken(TokenType.LITERAL_OCTAL);
        }
        if (currentChar() == '0' && (peek(1) == 'b' || peek(1) == 'B')) {
            advance();
            advance();
            if (!isBinaryDigit(currentChar())) {
                error("Binary literal must have at least one digit");
            }
            consumeDigitsForBase(2);
            return makeToken(TokenType.LITERAL_BINARY);
        }

        // Decimal integer / double
        consumeDigitsForBase(10);

        boolean isDouble = false;
        if (currentChar() == '.' && isDecimalDigit(peek(1))) {
            isDouble = true;
            advance();
            consumeDigitsForBase(10);
        }
        if (currentChar() == 'e' || currentChar() == 'E') {
            isDouble = true;
            advance();
            if (currentChar() == '+' || currentChar() == '-') {
                advance();
            }
            if (!isDecimalDigit(currentChar())) {
                error("Exponent has no digits");
            }
            while (isDecimalDigit(currentChar())) {
                advance();
            }
        }

        return makeToken(isDouble ? TokenType.LITERAL_DOUBLE : TokenType.LITERAL_INTEGER);
    }

    /**
     * @return A Token object representing the next token in the source
     *         code, which is a string literal.
     *         Handles common escape sequences (\n, \t, \r, \\, \", \', \0, \b, \f).
     */
    private Token lexString() {
        advance(); // opening "
        while (indexInSourceCode < sourceCode.length()) {
            char currentChar = currentChar();
            if (currentChar == '"') {
                advance();
                return makeToken(TokenType.LITERAL_STRING);
            }
            if (currentChar == '\n') {
                error("Unterminated string literal");
                return makeToken(TokenType.SPECIAL_UNKNOWN);
            }
            if (currentChar == '\\') {
                advance();
                if (indexInSourceCode >= sourceCode.length()) {
                    break;
                }
                char esc = currentChar();
                if (!isValidEscape(esc)) {
                    error("Invalid escape sequence: \\" + esc);
                }
                advance();
                continue;
            }
            advance();
        }
        error("Unterminated string literal");
        return makeToken(TokenType.SPECIAL_UNKNOWN);
    }

    /**
     * @return A Token object representing the next token in the source
     *         code, which is a char literal.
     *         Supports single character or an escaped character between single
     *         quotes.
     */
    private Token lexChar() {
        advance(); // opening '
        if (indexInSourceCode >= sourceCode.length() || currentChar() == '\n') {
            error("Unterminated char literal");
            return makeToken(TokenType.SPECIAL_UNKNOWN);
        }
        if (currentChar() == '\'') {
            error("Empty char literal");
            advance();
            return makeToken(TokenType.SPECIAL_UNKNOWN);
        }
        if (currentChar() == '\\') {
            advance();
            if (indexInSourceCode >= sourceCode.length()) {
                error("Unterminated char literal");
                return makeToken(TokenType.SPECIAL_UNKNOWN);
            }
            char esc = currentChar();
            if (!isValidEscape(esc)) {
                error("Invalid escape sequence: \\" + esc);
            }
            advance();
        } else {
            advance();
        }
        if (indexInSourceCode >= sourceCode.length() || currentChar() != '\'') {
            error("Unterminated char literal");
            // Attempt to resynchronise at the next single quote or newline.
            while (indexInSourceCode < sourceCode.length()
                    && currentChar() != '\''
                    && currentChar() != '\n') {
                advance();
            }
            if (indexInSourceCode < sourceCode.length() && currentChar() == '\'') {
                advance();
            }
            return makeToken(TokenType.SPECIAL_UNKNOWN);
        }
        advance(); // closing '
        return makeToken(TokenType.LITERAL_CHAR);
    }

    /**
     * @return returns a Token object representing the next token in the source
     *         code, which is a symbol literal.
     *         Handles all operators and delimiters defined in TokenType.
     *         Multi-character operators are matched greedily.
     */
    private Token lexSymbol() {
        char currentChar = currentChar();

        // single-character symbols that need no lookahead.
        TokenType direct = SINGLE_CHAR_SYMBOLS.get(currentChar);
        if (direct != null) {
            advance();
            return makeToken(direct);
        }

        // Ambiguous operators that depend on the next 1-2 characters.
        switch (currentChar) {
            case '+':
                return handleTripleOperator('+', TokenType.OPERATOR_PLUS,
                        TokenType.OPERATOR_INCREMENT, TokenType.OPERATOR_PLUS_ASSIGN);
            case '-':
                return handleTripleOperator('-', TokenType.OPERATOR_MINUS,
                        TokenType.OPERATOR_DECREMENT, TokenType.OPERATOR_MINUS_ASSIGN);
            case '*':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_MULTIPLY,
                        TokenType.OPERATOR_MULTIPLY_ASSIGN);
            case '/':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_DIVIDE,
                        TokenType.OPERATOR_DIVIDE_ASSIGN);
            case '%':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_MODULO,
                        TokenType.OPERATOR_MODULO_ASSIGN);
            case '=':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_ASSIGN,
                        TokenType.OPERATOR_EQUALS);
            case '!':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_LOGICAL_NOT,
                        TokenType.OPERATOR_NOT_EQUALS);
            case '<':
                return handleShiftOrRelationalOperator('<', TokenType.OPERATOR_LESS_THAN,
                        TokenType.OPERATOR_LESS_EQUAL,
                        TokenType.OPERATOR_BITSHIFT_LEFT,
                        TokenType.OPERATOR_BITSHIFT_LEFT_ASSIGN);
            case '>':
                return handleShiftOrRelationalOperator('>', TokenType.OPERATOR_GREATER_THAN,
                        TokenType.OPERATOR_GREATER_EQUAL,
                        TokenType.OPERATOR_BITSHIFT_RIGHT,
                        TokenType.OPERATOR_BITSHIFT_RIGHT_ASSIGN);
            case '&':
                return handleTripleOperator('&', TokenType.OPERATOR_BITWISE_AND,
                        TokenType.OPERATOR_AND, TokenType.OPERATOR_BITWISE_AND_ASSIGN);
            case '|':
                return handleTripleOperator('|', TokenType.OPERATOR_BITWISE_OR,
                        TokenType.OPERATOR_OR, TokenType.OPERATOR_BITWISE_OR_ASSIGN);
            case '^':
                return handleSingleOrAssignOperator(TokenType.OPERATOR_BITWISE_XOR,
                        TokenType.OPERATOR_BITWISE_XOR_ASSIGN);
            default:
                error("Unexpected character: '" + currentChar + "'");
                advance();
                return makeToken(TokenType.SPECIAL_UNKNOWN);
        }
    }

    /**
     * Handles the "{@code op} or {@code op=}" pattern. The leading character
     * has already been peeked at by the caller; this helper consumes it and
     * greedily attaches an {@code =} if present.
     *
     * @param single     token type for the bare operator ({@code *}, {@code /}, ...)
     * @param withAssign token type for the compound-assign form ({@code *=}, {@code /=}, ...)
     */
    private Token handleSingleOrAssignOperator(TokenType single, TokenType withAssign) {
        advance();
        if (currentChar() == '=') {
            advance();
            return makeToken(withAssign);
        }
        return makeToken(single);
    }

    /**
     * Handles the "{@code op} / {@code op op} / {@code op=}" pattern used by
     * {@code + ++ +=}, {@code - -- -=}, {@code & && &=}, {@code | || |=}.
     *
     * @param doubled     the character that, when repeated, yields {@code doubledType}
     * @param single      token type for the bare operator
     * @param doubledType token type for the doubled operator ({@code ++}, {@code &&}, ...)
     * @param withAssign  token type for the compound-assign form
     */
    private Token handleTripleOperator(char doubled, TokenType single, TokenType doubledType, TokenType withAssign) {
        advance();
        if (currentChar() == doubled) {
            advance();
            return makeToken(doubledType);
        }
        if (currentChar() == '=') {
            advance();
            return makeToken(withAssign);
        }
        return makeToken(single);
    }

    /**
     * Handles the four-way "{@code op} / {@code op=} / {@code op op} / {@code op op=}"
     * pattern used by {@code <} and {@code >}, which can be a relational
     * operator, a relational-equal, a bit-shift, or a shift-assign.
     *
     * @param same        the character that, when repeated, forms a shift
     * @param single      token type for the bare relational operator ({@code <}, {@code >})
     * @param withAssign  token type for the relational-equal ({@code <=}, {@code >=})
     * @param shift       token type for the shift operator ({@code <<}, {@code >>})
     * @param shiftAssign token type for the shift-assign ({@code <<=}, {@code >>=})
     */
    private Token handleShiftOrRelationalOperator(char same, TokenType single, TokenType withAssign,
                                    TokenType shift, TokenType shiftAssign) {
        advance();
        if (currentChar() == '=') {
            advance();
            return makeToken(withAssign);
        }
        if (currentChar() == same) {
            advance();
            if (currentChar() == '=') {
                advance();
                return makeToken(shiftAssign);
            }
            return makeToken(shift);
        }
        return makeToken(single);
    }

    /**
     * @return returns a Token object representing the next token in the source
     *         code, which is a keyword or an identifier.
     *         The method will read characters until it encounters a non-identifier
     *         character, then check if the resulting string matches any known
     *         keywords.
     *         If it does, it will return a keyword token; otherwise, it will return
     *         an identifier token.
     *         The literals {@code true}, {@code false} and {@code null} are
     *         recognised here and emitted as LITERAL_BOOLEAN / LITERAL_NULL.
     */
    private Token lexIdentifierOrKeyword() {
        while (isIdentifierPart(currentChar())) {
            advance();
        }
        String keywordText = sourceCode.substring(tokenStartIndex, indexInSourceCode);
        TokenType keywordType = KEYWORDS.get(keywordText);
        if (keywordType != null) {
            return makeToken(keywordType);
        }
        switch (keywordText) {
            case "true":
            case "false":
                return makeToken(TokenType.LITERAL_BOOLEAN);
            case "null":
                return makeToken(TokenType.LITERAL_NULL);
            default:
                return makeToken(TokenType.ID_IDENTIFIER);
        }
    }

    /**
     * @return returns the current character in the source code at the current
     *         index. If the index is out of bounds (i.e., past the end of the
     *         source code), it returns a null character ('\0') to indicate the end
     *         of input.
     *         This method is used to safely access characters in the source code
     *         without risking an IndexOutOfBoundsException, and it allows the lexer
     *         to detect when it has reached the end of the source code.
     */
    private char currentChar() {
        return indexInSourceCode < sourceCode.length() ? sourceCode.charAt(indexInSourceCode) : '\0';
    }

    /**
     * @return The character at the specified offset from the current index in the
     *         source code. If the resulting index is out of bounds, it returns a
     *         null character ('\0').
     *         This method allows the lexer to look ahead in the source code without
     *         advancing the current position, which is useful for making decisions
     *         based on upcoming characters (e.g., distinguishing between '=' and
     *         '==').
     */
    private char peek(int offset) {
        return indexInSourceCode + offset < sourceCode.length() ? sourceCode.charAt(indexInSourceCode + offset) : '\0';
    }

    /**
     * This method advances the current index in the source code by one character,
     * updating the line and column counters accordingly. If the current character
     * is a newline ('\n'), it increments the line number and resets the column to
     * one; otherwise, it increments the column number.
     * This method is essential for moving through the source code while keeping
     * accurate track of the current position for error reporting and token
     * metadata.
     */
    private void advance() {
        if (indexInSourceCode < sourceCode.length()) {
            char currentChar = sourceCode.charAt(indexInSourceCode);
            indexInSourceCode++;
            if (currentChar == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
    }

    /**
     * @return returns true if the given character is a valid starting character for
     *         an identifier (i.e., a letter or an underscore), and false otherwise.
     *         This method is used to determine if the lexer should start lexing an
     *         identifier or keyword when it encounters a character.
     *         In many programming languages, identifiers must start with a letter
     *         (a-z, A-Z) or an underscore (_), and cannot start with a digit or
     *         other special characters. This method enforces that rule during
     *         tokenization.
     */
    private boolean isIdentifierStart(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    /**
     * @return returns true if the given character is a valid continuation character
     *         for an identifier (letter, digit, or underscore).
     *         Identifier continuation characters are broader than start characters
     *         since digits are allowed after the first character.
     */
    private boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || isDecimalDigit(c);
    }

    /**
     * @return returns true if the given character is a valid hexadecimal digit
     *         (i.e., 0-9, a-f, A-F), and false otherwise. This method is used to
     *         determine if a character can be part of a hexadecimal number literal
     *         during tokenization.
     *         Hexadecimal digits include the numbers 0 through 9 and the letters A
     *         through F (both uppercase and lowercase), which represent the values
     *         10 through 15. This method checks for those valid characters when
     *         lexing hexadecimal literals.
     *         For example, in a hexadecimal literal like "0x1A3F", the characters
     *         '1', 'A', '3', and 'F' would all return true when passed to this
     *         method.
     */
    private boolean isHexDigit(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    /**
     * @return returns true if the given character is a valid decimal digit (i.e.,
     *         0-9), and false otherwise. This method is used to determine if a
     *         character can be part of a decimal number literal during
     *         tokenization.
     *         Decimal digits include the numbers 0 through 9. This method checks
     *         for those valid characters when lexing decimal literals.
     *         For example, in a decimal literal like "12345", the characters '1',
     *         '2', '3', '4', and '5' would all return true when passed to this
     *         method.
     */
    private boolean isDecimalDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * @return returns true if the given character is a valid octal digit (i.e.,
     *         0-7), and false otherwise. This method is used to determine if a
     *         character can be part of an octal number literal during tokenization.
     *         Octal digits include the numbers 0 through 7. This method checks for
     *         those valid characters when lexing octal literals.
     *         For example, in an octal literal like "0o0755", the characters '0',
     *         '7', '5', and '5' would all return true when passed to this method,
     *         while '8' or '9' would return false.
     */
    private boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }

    /**
     * @return returns true if the given character is a valid binary digit (i.e., 0
     *         or 1), and false otherwise. This method is used to determine if a
     *         character can be part of a binary number literal during tokenization.
     *         Binary digits include only the numbers 0 and 1. This method checks
     *         for those valid characters when lexing binary literals.
     *         For example, in a binary literal like "0b1010", the characters '1'
     *         and '0' would return true when passed to this method, while any other
     *         character would return false.
     */
    private boolean isBinaryDigit(char c) {
        return c == '0' || c == '1';
    }

    /**
     * @param c    the character to check
     * @param base the base of the number system (only 2, 8, 10, 16)
     * @return returns true if the given character is a valid digit for the
     *         specified base (2, 8, 10, or 16), and false otherwise. This method is
     *         used to determine if a character can be part of a number literal in
     *         the given base during tokenization.
     *         The method uses a switch statement to check the base and calls the
     *         appropriate helper method (isBinaryDigit, isOctalDigit,
     *         isDecimalDigit, or isHexDigit) to validate the character based on the
     *         specified base.
     *         For example, if the base is 16, this method will return true for
     *         characters '0' through '9', 'a' through 'f', and 'A' through 'F',
     *         while it will return false for any other character.
     */
    private boolean isDigitForBase(char c, int base) {
        return switch (base) {
            case 2 -> isBinaryDigit(c);
            case 8 -> isOctalDigit(c);
            case 10 -> isDecimalDigit(c);
            case 16 -> isHexDigit(c);
            default -> false;
        };
    }

    /**
     * Consumes a digit sequence for the given base. Underscores are allowed
     * ONLY as separators between two digits — a leading, trailing, or repeated
     * underscore is not part of the literal and terminates the digit sequence.
     * This means {@code 100_000_000} is fully consumed, but {@code 100_} stops
     * after {@code 100} (leaving the underscore for the next lex call), and
     * {@code 100__000} stops after the first {@code 100}.
     *
     * @param base the numeric base (2, 8, 10, or 16)
     */
    private void consumeDigitsForBase(int base) {
        while (true) {
            char c = currentChar();
            if (isDigitForBase(c, base)) {
                advance();
            } else if (c == '_' && isDigitForBase(peek(1), base)) {
                advance();
            } else {
                break;
            }
        }
    }

    /**
     * @return returns true if the given character is a recognised escape sequence
     *         character inside a string or char literal.
     *         Recognised escapes: n, r, t, b, f, 0, ", ', \.
     */
    private boolean isValidEscape(char c) {
        return c == 'n' || c == 'r' || c == 't' || c == 'b' || c == 'f'
                || c == '0' || c == '"' || c == '\'' || c == '\\';
    }

}
