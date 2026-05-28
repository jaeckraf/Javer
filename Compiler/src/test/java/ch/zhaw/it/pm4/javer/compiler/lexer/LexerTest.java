package ch.zhaw.it.pm4.javer.compiler.lexer;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link Lexer}. Exercises every sub-lexer (identifiers,
 * numbers, strings, chars, symbols, comments), the main error branches, and
 * verifies every error path reports through the {@link DiagnosticBag}.
 */
class LexerTest {

    private DiagnosticBag diagnosticBag;

    @BeforeEach
    void setUp() {
        diagnosticBag = mock(DiagnosticBag.class);
    }

    private List<Token> lex(String source) {
        return new Lexer(source, diagnosticBag).lexSourcecode();
    }

    /** Lex a source that should yield exactly one real token plus an EOF. */
    private Token single(String source) {
        List<Token> tokens = lex(source);
        assertEquals(2, tokens.size(), "expected one real token + EOF, got " + tokens);
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(1).getTokenType());
        return tokens.get(0);
    }

    private void assertTypes(String source, TokenType... expected) {
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

    private void assertNoErrorDiagnostics() {
        verify(diagnosticBag, never()).add(any(SourceLocation.class),
                eq(Severity.ERROR),
                anyString());
    }

    private void assertErrorDiagnosticContains(String fragment) {
        ArgumentCaptor<String> messages = ArgumentCaptor.forClass(String.class);
        verify(diagnosticBag, atLeastOnce())
                .add(any(SourceLocation.class), eq(Severity.ERROR), messages.capture());
        assertTrue(messages.getAllValues().stream().anyMatch(m -> m.contains(fragment)),
                "expected an ERROR diagnostic containing \"" + fragment + "\", got: "
                        + messages.getAllValues());
    }

    private static String loadResource(String classpath) {
        try (InputStream in = LexerTest.class.getResourceAsStream(classpath)) {
            if (in == null) {
                throw new IllegalStateException("test resource not found: " + classpath);
            }
            return new String(in.readAllBytes(), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ---------------------------------------------------------------------
    // Construction / empty input
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Empty source produces only an EOF token and never touches the bag")
    void emptySource() {
        List<Token> tokens = lex("");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.getFirst().getTokenType());
        verifyNoInteractions(diagnosticBag);
    }

    @Test
    @DisplayName("Null source is treated as empty string")
    void nullSource() {
        List<Token> tokens = new Lexer(null, diagnosticBag).lexSourcecode();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.getFirst().getTokenType());
        verifyNoInteractions(diagnosticBag);
    }

    @Test
    @DisplayName("Constructor rejects a null DiagnosticBag")
    void rejectsNullDiagnosticBag() {
        assertThrows(NullPointerException.class, () -> new Lexer("x", null));
    }

    @Test
    @DisplayName("Only whitespace produces only an EOF token and no diagnostics")
    void onlyWhitespace() {
        List<Token> tokens = lex("   \t\n\r  \n");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.getFirst().getTokenType());
        verifyNoInteractions(diagnosticBag);
    }

    @Test
    @DisplayName("Valid input never reports an error diagnostic")
    void validInputProducesNoErrorDiagnostics() {
        lex("let x = 1 + 2;");
        assertNoErrorDiagnostics();
    }

    // ---------------------------------------------------------------------
    // Identifiers / keywords / boolean+null literals
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Identifiers are lexed correctly")
    void identifierTests() {
        String[] inputs = {
                "foo",
                "_my_var_42",
                "ifx"
        };

        for (String input : inputs) {
            Token t = single(input);

            assertEquals(TokenType.ID_IDENTIFIER, t.getTokenType());
            assertEquals(input, t.getValue());
        }
    }

    @Test
    @DisplayName("All control-flow keywords map to the correct TokenType")
    void controlFlowKeywords() {
        Object[][] cases = {
                {"if", TokenType.KEYWORD_IF},
                {"else", TokenType.KEYWORD_ELSE},
                {"while", TokenType.KEYWORD_WHILE},
                {"do", TokenType.KEYWORD_DO},
                {"for", TokenType.KEYWORD_FOR},
                {"return", TokenType.KEYWORD_RETURN},
                {"fn", TokenType.KEYWORD_FUNCTION},
                {"break", TokenType.KEYWORD_BREAK},
                {"continue", TokenType.KEYWORD_CONTINUE},
                {"switch", TokenType.KEYWORD_SWITCH},
                {"case", TokenType.KEYWORD_CASE},
                {"default", TokenType.KEYWORD_DEFAULT},
                {"let", TokenType.KEYWORD_LET},
                {"call", TokenType.KEYWORD_CALL},
                {"cast", TokenType.KEYWORD_CAST}
        };

        for (Object[] c : cases) {
            String input = (String) c[0];
            TokenType expected = (TokenType) c[1];

            assertEquals(expected, single(input).getTokenType());
        }
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
    @DisplayName("Double literal with fractional part")
    void doubleWithFraction() {
        Token t = single("3.14");
        assertEquals(TokenType.LITERAL_DOUBLE, t.getTokenType());
        assertEquals("3.14", t.getValue());
    }

    @Test
    @DisplayName("Integer followed by a dot that is NOT a fraction stays integer")
    void integerFollowedByNonFractionDot() {
        // "12.foo" -> 12 . foo (method/field access, not a double)
        assertTypes("12.foo",
                TokenType.LITERAL_INTEGER,
                TokenType.SYMBOL_DOT,
                TokenType.ID_IDENTIFIER);
    }

    @Test
    @DisplayName("Hex literal (both 0x and 0X prefix)")
    void hexLiteral() {
        Token lower = single("0xFF");
        assertEquals(TokenType.LITERAL_HEX, lower.getTokenType());
        assertEquals("FF", lower.getValue());

        Token upper = single("0X1a2B");
        assertEquals(TokenType.LITERAL_HEX, upper.getTokenType());
        assertEquals("1a2B", upper.getValue());
    }

    @Test
    @DisplayName("Octal literal")
    void octalLiteral() {
        Token t = single("0o755");
        assertEquals(TokenType.LITERAL_OCTAL, t.getTokenType());
        assertEquals("755", t.getValue());

        Token upper = single("0O17");
        assertEquals(TokenType.LITERAL_OCTAL, upper.getTokenType());
        assertEquals("17", upper.getValue());
    }

    @Test
    @DisplayName("Binary literal")
    void binaryLiteral() {
        Token t = single("0b1010");
        assertEquals(TokenType.LITERAL_BINARY, t.getTokenType());
        assertEquals("1010", t.getValue());

        Token upper = single("0B11000011");
        assertEquals(TokenType.LITERAL_BINARY, upper.getTokenType());
        assertEquals("11000011", upper.getValue());
    }

    // ---------------------------------------------------------------------
    // String literals: valid escape table
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Simple string literal")
    void simpleString() {
        Token t = single("\"hello\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
        assertEquals("hello", t.getValue());
    }

    @Test
    @DisplayName("Empty string literal")
    void emptyString() {
        Token t = single("\"\"");
        assertEquals(TokenType.LITERAL_STRING, t.getTokenType());
        assertEquals("", t.getValue());
    }

    @Test
    @DisplayName("Every valid escape sequence in a string is accepted with no error diagnostics")
    void everyValidEscapeSequenceInString() {
        // \n \r \t \b \f \0 \" \' \\
        String[][] escapes = {
                {"\\n", "\n"},
                {"\\r", "\r"},
                {"\\t", "\t"},
                {"\\b", "\b"},
                {"\\f", "\f"},
                {"\\0", "\0"},
                {"\\\"", "\""},
                {"\\'", "'"},
                {"\\\\", "\\"}
        };
        for (String[] escape : escapes) {
            String esc = escape[0];
            String expectedValue = escape[1];
            DiagnosticBag scopedBag = mock(DiagnosticBag.class);
            String source = "\"" + esc + "\"";
            List<Token> tokens = new Lexer(source, scopedBag).lexSourcecode();

            assertEquals(2, tokens.size(), "escape " + esc + " produced wrong token count");
            assertEquals(TokenType.LITERAL_STRING, tokens.getFirst().getTokenType(),
                    "escape " + esc + " did not produce LITERAL_STRING");
            assertEquals(expectedValue, tokens.getFirst().getValue(),
                    "escape " + esc + " was not resolved in the token value");
            assertEquals(1, tokens.getFirst().getValue().length(),
                    "escape " + esc + " should be stored as one character");
            verify(scopedBag, never())
                    .add(any(SourceLocation.class), eq(Severity.ERROR), anyString());
        }
    }

    // ---------------------------------------------------------------------
    // Char literals
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Simple char literal")
    void simpleChar() {
        Token t = single("'a'");
        assertEquals(TokenType.LITERAL_CHAR, t.getTokenType());
        assertEquals("a", t.getValue());
    }

    @Test
    @DisplayName("Escaped char literal")
    void escapedChar() {
        Token t = single("'\\n'");
        assertEquals(TokenType.LITERAL_CHAR, t.getTokenType());
        assertEquals("\n", t.getValue());
        assertEquals(1, t.getValue().length());
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
        assertEquals(TokenType.SYMBOL_ELLIPSIS, single("...").getTokenType());
        assertEquals(TokenType.SYMBOL_DOT, single(".").getTokenType());
        assertEquals(TokenType.SYMBOL_COLON, single(":").getTokenType());
        assertEquals(TokenType.SYMBOL_QUESTION_MARK, single("?").getTokenType());
    }

    // ---------------------------------------------------------------------
    // Whitespace and comments
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Line comment is skipped")
    void lineComment() {
        assertTypes("// hello\nfoo", TokenType.ID_IDENTIFIER);
        assertNoErrorDiagnostics();
    }

    @Test
    @DisplayName("Line comment at EOF without trailing newline is skipped")
    void lineCommentAtEof() {
        List<Token> tokens = lex("// last line");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.SPECIAL_END_OF_FILE, tokens.getFirst().getTokenType());
        assertNoErrorDiagnostics();
    }

    @Test
    @DisplayName("Block comment is skipped")
    void blockComment() {
        assertTypes("foo /* in the middle */ bar",
                TokenType.ID_IDENTIFIER,
                TokenType.ID_IDENTIFIER);
        assertNoErrorDiagnostics();
    }

    @Test
    @DisplayName("Multi-line block comment is skipped")
    void multiLineBlockComment() {
        assertTypes("foo\n/* multi\n  line\r\n  comment */\nbar",
                TokenType.ID_IDENTIFIER,
                TokenType.ID_IDENTIFIER);
        assertNoErrorDiagnostics();
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
    @DisplayName("Simple function declaration with return type lexes into the expected token sequence")
    void simpleFunctionDeclaration() {
        assertTypes("fn void main() {}",
                TokenType.KEYWORD_FUNCTION,
                TokenType.TYPE_VOID,
                TokenType.ID_IDENTIFIER,
                TokenType.SYMBOL_LEFT_PARENTHESIS,
                TokenType.SYMBOL_RIGHT_PARENTHESIS,
                TokenType.SYMBOL_LEFT_BRACE,
                TokenType.SYMBOL_RIGHT_BRACE);
    }

    @Test
    @DisplayName("SourceLocation uses 1-indexed, inclusive start/end columns on a single line")
    void sourceLocationColumnsAreInclusiveAndOneBased() {
        // "ab cd" -> "ab" at columns 1..2, space at 3, "cd" at columns 4..5
        List<Token> tokens = lex("ab cd");

        SourceLocation first = tokens.getFirst().getPosition();
        assertEquals(1, first.lineNumber());
        assertEquals(1, first.startColumn(), "'ab' should start at column 1");
        assertEquals(2, first.endColumn(), "'ab' should end at column 2 (inclusive)");

        SourceLocation second = tokens.get(1).getPosition();
        assertEquals(1, second.lineNumber());
        assertEquals(4, second.startColumn(), "'cd' should start at column 4");
        assertEquals(5, second.endColumn(), "'cd' should end at column 5 (inclusive)");
    }

    @Test
    @DisplayName("Tab always snaps the next token to the next tab stop (width 4)")
    void tabAlwaysSnapsToNextTabStop() {
        // regardless of starting character tab mus be same
        assertEquals(5, lex("\ta").getFirst().getPosition().startColumn(),
                "tab at col 1 should land 'a' at col 5");
        assertEquals(5, lex(" \ta").getFirst().getPosition().startColumn(),
                "tab at col 2 should land 'a' at col 5");
        assertEquals(5, lex("  \ta").getFirst().getPosition().startColumn(),
                "tab at col 3 should land 'a' at col 5");
        assertEquals(5, lex("   \ta").getFirst().getPosition().startColumn(),
                "tab at col 4 should land 'a' at col 5");
        assertEquals(9, lex("    \ta").getFirst().getPosition().startColumn(),
                "tab at col 5 should land 'a' at col 9");
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
                tokens.getLast().getTokenType());
        for (int i = 0; i < tokens.size() - 1; i++) {
            assertNotEquals(TokenType.SPECIAL_END_OF_FILE, tokens.get(i).getTokenType());
        }
    }

    // ---------------------------------------------------------------------
    // Negative tests: each error path must report through the DiagnosticBag
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Lexing errors produce SPECIAL_UNKNOWN and diagnostics")
    void lexingErrors() {

        record Case(String input, String expectedMessage) {}

        Case[] cases = {
                new Case("\"oops", "Unterminated string"),
                new Case("''", "Empty char literal"),
                new Case("'a", "Unterminated char"),
                new Case("'ab'", "Unterminated char"),
                new Case("@", "Unexpected character")
        };

        for (Case c : cases) {
            Token t = single(c.input());

            assertEquals(TokenType.SPECIAL_UNKNOWN, t.getTokenType());
            assertErrorDiagnosticContains(c.expectedMessage());
        }
    }

    @Test
    @DisplayName("Newline inside a string reports an error diagnostic")
    void newlineInStringReportsError() {
        List<Token> tokens = lex("\"oh\nno\"");
        assertEquals(TokenType.SPECIAL_UNKNOWN, tokens.getFirst().getTokenType());
        assertErrorDiagnosticContains("Unterminated string");
    }

    @Test
    @DisplayName("Every invalid string escape character produces an 'Invalid escape' diagnostic")
    void everyInvalidEscapeSequenceInStringReportsError() {
        String[] invalid = { "\\q", "\\x", "\\y", "\\z", "\\1", "\\a" };
        for (String esc : invalid) {
            DiagnosticBag scopedBag = mock(DiagnosticBag.class);
            String source = "\"" + esc + "\"";
            List<Token> tokens = new Lexer(source, scopedBag).lexSourcecode();

            assertEquals(TokenType.LITERAL_STRING, tokens.getFirst().getTokenType(),
                    "escape " + esc + " did not recover to LITERAL_STRING");
            verify(scopedBag, atLeastOnce()).add(
                    any(SourceLocation.class),
                    eq(Severity.ERROR),
                    contains("Invalid escape"));
        }
    }

    @Test
    @DisplayName("Invalid escape in char literal reports 'Invalid escape'")
    void invalidEscapeInCharReportsError() {
        lex("'\\q'");
        assertErrorDiagnosticContains("Invalid escape");
    }

    @Test
    @DisplayName("Hex literal with no digits reports an error")
    void emptyHexLiteralReportsError() {
        lex("0x");
        assertErrorDiagnosticContains("Hexadecimal literal must have at least one digit");
    }

    @Test
    @DisplayName("Octal literal with no digits reports an error")
    void emptyOctalLiteralReportsError() {
        lex("0o");
        assertErrorDiagnosticContains("Octal literal must have at least one digit");
    }

    @Test
    @DisplayName("Binary literal with no digits reports an error")
    void emptyBinaryLiteralReportsError() {
        lex("0b");
        assertErrorDiagnosticContains("Binary literal must have at least one digit");
    }

    @Test
    @DisplayName("Trailing dot on an integer lexes as INT + DOT for method/field access")
    void trailingDotOnInteger() {
        // "10." is not flagged: the same pattern underpins "12.foo" (field access).
        assertTypes("10.", TokenType.LITERAL_INTEGER, TokenType.SYMBOL_DOT);
        assertNoErrorDiagnostics();
    }

    @Test
    @DisplayName("Consecutive decimal points in a number literal report an error")
    void consecutiveDecimalPointsReportError() {
        lex("10..2");
        assertErrorDiagnosticContains("consecutive decimal points");
    }

    @Test
    @DisplayName("Double literal with a second decimal point reports an error")
    void multipleDecimalPointsInDoubleReportError() {
        lex("10.2.3");
        assertErrorDiagnosticContains("too many decimal points");
    }

    @Test
    @DisplayName("Unterminated block comment reports an error diagnostic")
    void unterminatedBlockCommentReportsError() {
        assertDoesNotThrow(() -> lex("/* never closed"));
        assertErrorDiagnosticContains("Unterminated block comment");
    }

    // ---------------------------------------------------------------------
    // Comprehensive fixture: every lexable TokenType in one file
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Fixture file covers every lexable TokenType without diagnostics")
    void allTokenTypesFromResourceFile() {
        String source = loadResource("all-tokens.javer");
        List<Token> tokens = lex(source);

        Set<TokenType> seen = new HashSet<>();
        for (Token token : tokens) {
            seen.add(token.getTokenType());
        }

        assertTrue(seen.contains(TokenType.SPECIAL_END_OF_FILE), "fixture missing EOF token");
        assertFalse(seen.contains(TokenType.SPECIAL_UNKNOWN),
                "fixture unexpectedly contained SPECIAL_UNKNOWN tokens: " + tokens);
        assertNoErrorDiagnostics();

        Set<TokenType> missing = EnumSet.allOf(TokenType.class);
        missing.remove(TokenType.SPECIAL_UNKNOWN);
        missing.removeAll(seen);
        assertTrue(missing.isEmpty(), "token types missing from fixture: " + missing);
    }
}
