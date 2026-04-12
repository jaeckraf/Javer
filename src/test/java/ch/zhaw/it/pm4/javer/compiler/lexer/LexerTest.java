package ch.zhaw.it.pm4.javer.compiler.lexer;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Lexer}. These tests exercise every sub-lexer
 * (identifiers, numbers, strings, chars, symbols, comments) and the main
 * error branches 
 */
class LexerTest {

    private static List<Token> lex(String source) {
        return new Lexer(source).lexSourcecode();
    }

    /** Lex a source that should yield exactly one real token plus an EOF. */
    private static Token single(String source) {
        List<Token> tokens = lex(source);
        assertEquals(2, tokens.size(), "expected one real token + EOF, got " + tokens);
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(1).getTokenType());
        return tokens.get(0);
    }

    private static void assertTypes(String source, TokenType... expected) {
        List<Token> tokens = lex(source);
        assertEquals(expected.length + 1, tokens.size(),
                "unexpected token count for: " + source + " -> " + tokens);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).getTokenType(),
                    "token " + i + " mismatch for: " + source);
        }
        assertEquals(TokenType.SPECIAL_END_OF_FILE,
                tokens.get(expected.length).getTokenType());
    }

    // ---------------------------------------------------------------------
    // Construction / empty input
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Empty source produces only an EOF token")
    void emptySource() {
        List<Token> tokens = lex("");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(0).getTokenType());
    }

    @Test
    @DisplayName("Null source is treated as empty string")
    void nullSource() {
        List<Token> tokens = new Lexer(null).lexSourcecode();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(0).getTokenType());
    }

    @Test
    @DisplayName("Only whitespace produces only an EOF token")
    void onlyWhitespace() {
        List<Token> tokens = lex("   \t\n\r  \n");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(0).getTokenType());
    }

    @Test
    @DisplayName("Lexer accepts a non-null DiagnosticBag without crashing")
    void acceptsDiagnosticBag() {
        DiagnosticBag bag = new DiagnosticBag("<test>", 50, CompilationPhase.LEXING);
        List<Token> tokens = new Lexer("let x = 1;", bag).lexSourcecode();
        assertEquals(TokenType.KEYWORD_LET, tokens.get(0).getTokenType());
        assertEquals(TokenType.ID_IDENTIFIER, tokens.get(1).getTokenType());
    }

    // ---------------------------------------------------------------------
    // Identifiers / keywords / boolean+null literals
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Simple identifier is lexed as ID_IDENTIFIER")
    void simpleIdentifier() {
        Token t = single("foo");
        assertEquals(TokenType.ID_IDENTIFIER, t.getTokenType());
        assertEquals("foo", t.getValue());
    }

    @Test
    @DisplayName("Identifier may contain digits and underscores after the first char")
    void identifierWithDigitsAndUnderscores() {
        Token t = single("_my_var_42");
        assertEquals(TokenType.ID_IDENTIFIER, t.getTokenType());
        assertEquals("_my_var_42", t.getValue());
    }

    @Test
    @DisplayName("All control-flow keywords map to the correct TokenType")
    void controlFlowKeywords() {
        assertEquals(TokenType.KEYWORD_IF, single("if").getTokenType());
        assertEquals(TokenType.KEYWORD_ELSE, single("else").getTokenType());
        assertEquals(TokenType.KEYWORD_WHILE, single("while").getTokenType());
        assertEquals(TokenType.KEYWORD_DO, single("do").getTokenType());
        assertEquals(TokenType.KEYWORD_FOR, single("for").getTokenType());
        assertEquals(TokenType.KEYWORD_RETURN, single("return").getTokenType());
        assertEquals(TokenType.KEYWORD_FUNCTION, single("function").getTokenType());
        assertEquals(TokenType.KEYWORD_BREAK, single("break").getTokenType());
        assertEquals(TokenType.KEYWORD_CONTINUE, single("continue").getTokenType());
        assertEquals(TokenType.KEYWORD_SWITCH, single("switch").getTokenType());
        assertEquals(TokenType.KEYWORD_CASE, single("case").getTokenType());
        assertEquals(TokenType.KEYWORD_DEFAULT, single("default").getTokenType());
        assertEquals(TokenType.KEYWORD_LET, single("let").getTokenType());
        assertEquals(TokenType.KEYWORD_CALL, single("call").getTokenType());
    }

    @Test
    @DisplayName("All type keywords map to the correct TokenType")
    void typeKeywords() {
        assertEquals(TokenType.TYPE_STRUCT, single("struct").getTokenType());
        assertEquals(TokenType.TYPE_INTEGER, single("int").getTokenType());
        assertEquals(TokenType.TYPE_DOUBLE, single("double").getTokenType());
        assertEquals(TokenType.TYPE_BOOLEAN, single("boolean").getTokenType());
        assertEquals(TokenType.TYPE_STRING, single("string").getTokenType());
        assertEquals(TokenType.TYPE_CHARACTER, single("char").getTokenType());
        assertEquals(TokenType.TYPE_VOID, single("void").getTokenType());
        assertEquals(TokenType.TYPE_ENUM, single("enum").getTokenType());
    }

    @Test
    @DisplayName("true / false lex as LITERAL_BOOLEAN, null as LITERAL_NULL")
    void booleanAndNullLiterals() {
        assertEquals(TokenType.LITERAL_BOOLEAN, single("true").getTokenType());
        assertEquals(TokenType.LITERAL_BOOLEAN, single("false").getTokenType());
        assertEquals(TokenType.LITERAL_NULL, single("null").getTokenType());
    }

    @Test
    @DisplayName("A keyword prefix followed by identifier chars is one identifier")
    void keywordPrefixIsIdentifier() {
        // "ifx" must be a single ID_IDENTIFIER, not KEYWORD_IF + ID_IDENTIFIER.
        Token t = single("ifx");
        assertEquals(TokenType.ID_IDENTIFIER, t.getTokenType());
        assertEquals("ifx", t.getValue());
    }

    // ---------------------------------------------------------------------
    // Numbers: integer / hex / octal / binary / double
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Decimal integer literal")
    void decimalInteger() {
        Token t = single("12345");
        assertEquals(TokenType.LITERAL_INTEGER, t.getTokenType());
        assertEquals("12345", t.getValue());
    }

    @Test
    @DisplayName("Decimal integer with underscore separators between digits")
    void decimalIntegerWithUnderscores() {
        Token t = single("100_000_000");
        assertEquals(TokenType.LITERAL_INTEGER, t.getTokenType());
        assertEquals("100_000_000", t.getValue());
    }

    @Test
    @DisplayName("Trailing underscore is NOT part of the number literal")
    void trailingUnderscoreNotConsumed() {
        assertTypes("100_", TokenType.LITERAL_INTEGER, TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Consecutive underscores terminate the number at the first one")
    void consecutiveUnderscoresStopNumber() {
        assertTypes("100__000", TokenType.LITERAL_INTEGER, TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Double literal with fractional part")
    void doubleWithFraction() {
        Token t = single("3.14");
        assertEquals(TokenType.LITERAL_DOUBLE, t.getTokenType());
        assertEquals("3.14", t.getValue());
    }

    @Test
    @DisplayName("Double literal with exponent (e, E, +, -)")
    void doubleWithExponent() {
        assertEquals(TokenType.LITERAL_DOUBLE, single("1e5").getTokenType());
        assertEquals(TokenType.LITERAL_DOUBLE, single("1E5").getTokenType());
        assertEquals(TokenType.LITERAL_DOUBLE, single("1e+5").getTokenType());
        assertEquals(TokenType.LITERAL_DOUBLE, single("1e-5").getTokenType());
        assertEquals(TokenType.LITERAL_DOUBLE, single("3.14e10").getTokenType());
    }

    @Test
    @DisplayName("Integer followed by a dot that is NOT a fraction stays integer")
    void integerFollowedByNonFractionDot() {
        // "12.foo" -> 12 . foo  (method/field access, not a double)
        assertTypes("12.foo",
                TokenType.LITERAL_INTEGER,
                TokenType.SYMBOL_DOT,
                TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Hex literal (both 0x and 0X prefix, with separators)")
    void hexLiteral() {
        Token lower = single("0xFF");
        assertEquals(TokenType.LITERAL_HEX, lower.getTokenType());
        assertEquals("0xFF", lower.getValue());

        Token upper = single("0X1a2B");
        assertEquals(TokenType.LITERAL_HEX, upper.getTokenType());

        Token sep = single("0xDEAD_BEEF");
        assertEquals(TokenType.LITERAL_HEX, sep.getTokenType());
        assertEquals("0xDEAD_BEEF", sep.getValue());
    }

    @Test
    @DisplayName("Octal literal")
    void octalLiteral() {
        Token t = single("0o755");
        assertEquals(TokenType.LITERAL_OCTAL, t.getTokenType());
        assertEquals("0o755", t.getValue());
        assertEquals(TokenType.LITERAL_OCTAL, single("0O17").getTokenType());
    }

    @Test
    @DisplayName("Binary literal")
    void binaryLiteral() {
        Token t = single("0b1010");
        assertEquals(TokenType.LITERAL_BINARY, t.getTokenType());
        assertEquals("0b1010", t.getValue());
        assertEquals(TokenType.LITERAL_BINARY, single("0B1100_0011").getTokenType());
    }

    @Test
    @DisplayName("Empty base-prefixed literals and empty exponents do not crash")
    void emptyBaseLiteralsDoNotCrash() {
        // These report errors via the diagnostic bag but must still produce tokens.
        assertDoesNotThrow(() -> lex("0x"));
        assertDoesNotThrow(() -> lex("0o"));
        assertDoesNotThrow(() -> lex("0b"));
        assertDoesNotThrow(() -> lex("1e"));
    }

    // ---------------------------------------------------------------------
    // String literals
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Simple string literal")
    void simpleString() {
        Token t = single("\"hello\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
        assertEquals("\"hello\"", t.getValue());
    }

    @Test
    @DisplayName("Empty string literal")
    void emptyString() {
        Token t = single("\"\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
    }

    @Test
    @DisplayName("String with valid escape sequences")
    void stringWithEscapes() {
        Token t = single("\"a\\nb\\tc\\\"d\\\\e\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
    }

    @Test
    @DisplayName("Unterminated string literal produces SPECIAL_UNKNOWN")
    void unterminatedString() {
        Token t = single("\"oops");
        assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
    }

    @Test
    @DisplayName("Newline inside string terminates the literal as SPECIAL_UNKNOWN")
    void newlineInString() {
        List<Token> tokens = lex("\"oh\nno\"");
        assertEquals(TokenType.SPECIAL_UNKNOWN, tokens.get(0).getTokenType());
    }

    @Test
    @DisplayName("Invalid escape in a string still produces a LITERAL_STRING token")
    void invalidEscapeInString() {
        Token t = single("\"\\q\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
    }

    // ---------------------------------------------------------------------
    // Char literals
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Simple char literal")
    void simpleChar() {
        Token t = single("'a'");
        assertEquals(TokenType.LITERAL_CHAR, t.getTokenType());
        assertEquals("'a'", t.getValue());
    }

    @Test
    @DisplayName("Escaped char literal")
    void escapedChar() {
        Token t = single("'\\n'");
        assertEquals(TokenType.LITERAL_CHAR, t.getTokenType());
    }

    @Test
    @DisplayName("Empty char literal produces SPECIAL_UNKNOWN")
    void emptyChar() {
        Token t = single("''");
        assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
    }

    @Test
    @DisplayName("Unterminated char literal produces SPECIAL_UNKNOWN")
    void unterminatedChar() {
        Token t = single("'a");
        assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
    }

    @Test
    @DisplayName("Multi-character char literal produces SPECIAL_UNKNOWN")
    void multiCharLiteral() {
        Token t = single("'ab'");
        assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
    }

    // ---------------------------------------------------------------------
    // Operators
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Arithmetic and compound-assign arithmetic operators")
    void arithmeticOperators() {
        assertEquals(TokenType.OPERATOR_PLUS, single("+").getTokenType());
        assertEquals(TokenType.OPERATOR_INCREMENT, single("++").getTokenType());
        assertEquals(TokenType.OPERATOR_PLUS_ASSIGN, single("+=").getTokenType());
        assertEquals(TokenType.OPERATOR_MINUS, single("-").getTokenType());
        assertEquals(TokenType.OPERATOR_DECREMENT, single("--").getTokenType());
        assertEquals(TokenType.OPERATOR_MINUS_ASSIGN, single("-=").getTokenType());
        assertEquals(TokenType.OPERATOR_MULTIPLY, single("*").getTokenType());
        assertEquals(TokenType.OPERATOR_MULTIPLY_ASSIGN, single("*=").getTokenType());
        assertEquals(TokenType.OPERATOR_DIVIDE, single("/").getTokenType());
        assertEquals(TokenType.OPERATOR_DIVIDE_ASSIGN, single("/=").getTokenType());
        assertEquals(TokenType.OPERATOR_MODULO, single("%").getTokenType());
        assertEquals(TokenType.OPERATOR_MODULO_ASSIGN, single("%=").getTokenType());
    }

    @Test
    @DisplayName("Assignment, equality, relational and logical-not operators")
    void comparisonOperators() {
        assertEquals(TokenType.OPERATOR_ASSIGN, single("=").getTokenType());
        assertEquals(TokenType.OPERATOR_EQUALS, single("==").getTokenType());
        assertEquals(TokenType.OPERATOR_LOGICAL_NOT, single("!").getTokenType());
        assertEquals(TokenType.OPERATOR_NOT_EQUALS, single("!=").getTokenType());
        assertEquals(TokenType.OPERATOR_LESS_THAN, single("<").getTokenType());
        assertEquals(TokenType.OPERATOR_LESS_EQUAL, single("<=").getTokenType());
        assertEquals(TokenType.OPERATOR_GREATER_THAN, single(">").getTokenType());
        assertEquals(TokenType.OPERATOR_GREATER_EQUAL, single(">=").getTokenType());
    }

    @Test
    @DisplayName("Shift operators and their compound-assign forms")
    void shiftOperators() {
        assertEquals(TokenType.OPERATOR_BITSHIFT_LEFT, single("<<").getTokenType());
        assertEquals(TokenType.OPERATOR_BITSHIFT_LEFT_ASSIGN, single("<<=").getTokenType());
        assertEquals(TokenType.OPERATOR_BITSHIFT_RIGHT, single(">>").getTokenType());
        assertEquals(TokenType.OPERATOR_BITSHIFT_RIGHT_ASSIGN, single(">>=").getTokenType());
    }

    @Test
    @DisplayName("Bitwise and logical operators: & | ^ ~ and their pairs")
    void bitwiseOperators() {
        assertEquals(TokenType.OPERATOR_BITWISE_AND, single("&").getTokenType());
        assertEquals(TokenType.OPERATOR_AND, single("&&").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_AND_ASSIGN, single("&=").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_OR, single("|").getTokenType());
        assertEquals(TokenType.OPERATOR_OR, single("||").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_OR_ASSIGN, single("|=").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_XOR, single("^").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_XOR_ASSIGN, single("^=").getTokenType());
        assertEquals(TokenType.OPERATOR_BITWISE_NOT, single("~").getTokenType());
    }

    // ---------------------------------------------------------------------
    // Delimiters
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("All single-character delimiters")
    void delimiters() {
        assertEquals(TokenType.SYMBOL_LEFT_PARENTHESIS, single("(").getTokenType());
        assertEquals(TokenType.SYMBOL_RIGHT_PARENTHESIS, single(")").getTokenType());
        assertEquals(TokenType.SYMBOL_LEFT_BRACE, single("{").getTokenType());
        assertEquals(TokenType.SYMBOL_RIGHT_BRACE, single("}").getTokenType());
        assertEquals(TokenType.SYMBOL_LEFT_BRACKET, single("[").getTokenType());
        assertEquals(TokenType.SYMBOL_RIGHT_BRACKET, single("]").getTokenType());
        assertEquals(TokenType.SYMBOL_SEMICOLON, single(";").getTokenType());
        assertEquals(TokenType.SYMBOL_COMMA, single(",").getTokenType());
        assertEquals(TokenType.SYMBOL_DOT, single(".").getTokenType());
        assertEquals(TokenType.SYMBOL_COLON, single(":").getTokenType());
        assertEquals(TokenType.SYMBOL_QUESTION_MARK, single("?").getTokenType());
    }

    @Test
    @DisplayName("Unknown characters produce a SPECIAL_UNKNOWN token")
    void unknownCharacter() {
        Token t = single("@");
        assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
    }

    // ---------------------------------------------------------------------
    // Whitespace and comments
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Line comment is skipped")
    void lineComment() {
        assertTypes("// hello\nfoo", TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Line comment at EOF without trailing newline is skipped")
    void lineCommentAtEof() {
        List<Token> tokens = lex("// last line");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(0).getTokenType());
    }

    @Test
    @DisplayName("Block comment is skipped")
    void blockComment() {
        assertTypes("foo /* in the middle */ bar",
                TokenType.ID_IDENTIFIER,
                TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Multi-line block comment is skipped")
    void multiLineBlockComment() {
        assertTypes("foo\n/* multi\n  line\n  comment */\nbar",
                TokenType.ID_IDENTIFIER,
                TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Unterminated block comment does not crash")
    void unterminatedBlockComment() {
        assertDoesNotThrow(() -> lex("/* never closed"));
    }

    // ---------------------------------------------------------------------
    // Multi-token programs and source location tracking
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Small assignment program lexes into the expected token sequence")
    void smallProgram() {
        assertTypes("let x = 1 + 2;",
                TokenType.KEYWORD_LET,
                TokenType.ID_IDENTIFIER,
                TokenType.OPERATOR_ASSIGN,
                TokenType.LITERAL_INTEGER,
                TokenType.OPERATOR_PLUS,
                TokenType.LITERAL_INTEGER,
                TokenType.SYMBOL_SEMICOLON);
    }

    @Test
    @DisplayName("If-statement program lexes correctly")
    void ifStatement() {
        assertTypes("if (x == 0) { return; }",
                TokenType.KEYWORD_IF,
                TokenType.SYMBOL_LEFT_PARENTHESIS,
                TokenType.ID_IDENTIFIER,
                TokenType.OPERATOR_EQUALS,
                TokenType.LITERAL_INTEGER,
                TokenType.SYMBOL_RIGHT_PARENTHESIS,
                TokenType.SYMBOL_LEFT_BRACE,
                TokenType.KEYWORD_RETURN,
                TokenType.SYMBOL_SEMICOLON,
                TokenType.SYMBOL_RIGHT_BRACE);
    }

    @Test
    @DisplayName("SourceLocation tracks columns within a single line")
    void sourceLocationColumns() {
        List<Token> tokens = lex("ab cd");
        SourceLocation first = tokens.get(0).getPosition();
        SourceLocation second = tokens.get(1).getPosition();
        assertEquals(1, first.lineNumber());
        assertEquals(1, first.startColumn());
        assertEquals(2, first.endColumn());
        assertEquals(1, second.lineNumber());
        assertEquals(4, second.startColumn());
        assertEquals(5, second.endColumn());
    }

    @Test
    @DisplayName("SourceLocation tracks line numbers across newlines")
    void sourceLocationLines() {
        List<Token> tokens = lex("foo\nbar");
        assertEquals(1, tokens.get(0).getPosition().lineNumber());
        assertEquals(2, tokens.get(1).getPosition().lineNumber());
        assertEquals(1, tokens.get(1).getPosition().startColumn());
    }

    @Test
    @DisplayName("lexSourcecode always terminates with exactly one EOF token")
    void singleEofAtEnd() {
        List<Token> tokens = lex("a b c");
        assertEquals(TokenType.SPECIAL_END_OF_FILE,
                tokens.get(tokens.size() - 1).getTokenType());
        for (int i = 0; i < tokens.size() - 1; i++) {
            assertNotEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(i).getTokenType());
        }
    }
}
