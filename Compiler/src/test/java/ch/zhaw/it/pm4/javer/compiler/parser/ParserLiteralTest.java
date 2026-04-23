package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ParserLiteralTest {

    private DiagnosticBag diagnostics;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticBag("dummy.javer", 10, null, null);
    }

    private Parser createParser(Token... tokens) {
        return new Parser(List.of(tokens), diagnostics);
    }

    private Token token(TokenType type, String value) {
        return new Token(type, value, new SourceLocation(1, 1, 1));
    }

    private Object invokePrivate(Parser parser, String methodName) throws Exception {
        Method method = Parser.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(parser);
    }

    @Test
    void testParseInteger() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_INTEGER, "42"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.INT, expr.getKind());
        assertEquals(42, expr.getValue());
    }

    @Test
    void testParseHex() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_HEX, "0x1A"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.INT, expr.getKind());
        assertEquals(0x1A, expr.getValue());
    }

    @Test
    void testParseBinary() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_BINARY, "0b1010"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.INT, expr.getKind());
        assertEquals(0b1010, expr.getValue());
    }

    @Test
    void testParseOctal() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_OCTAL, "0o12"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.INT, expr.getKind());
        assertEquals(012, expr.getValue());
    }

    @Test
    void testParseDouble() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_DOUBLE, "3.14"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.DOUBLE, expr.getKind());
        assertEquals(3.14, expr.getValue());
    }

    @Test
    void testParseBooleanTrue() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_BOOLEAN, "true"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.BOOLEAN, expr.getKind());
        assertEquals(true, expr.getValue());
    }

    @Test
    void testParseBooleanFalse() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_BOOLEAN, "false"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.BOOLEAN, expr.getKind());
        assertEquals(false, expr.getValue());
    }

    @Test
    void testParseString() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_STRING, "\"hello\""), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.STRING, expr.getKind());
        assertEquals("hello", expr.getValue());
    }

    @Test
    void testParseChar() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_CHAR, "'a'"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.CHAR, expr.getKind());
        assertEquals('a', expr.getValue());
    }
    
    @Test
    void testParseCharEscaped() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_CHAR, "'\\n'"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.CHAR, expr.getKind());
        assertEquals('\n', expr.getValue());
    }

    @Test
    void testParseNull() throws Exception {
        Parser parser = createParser(token(TokenType.LITERAL_NULL, "null"), token(TokenType.SPECIAL_END_OF_FILE, ""));
        LiteralExpression<?> expr = (LiteralExpression<?>) invokePrivate(parser, "parseLiteralExpression");
        assertEquals(LiteralKind.NULL, expr.getKind());
        assertNull(expr.getValue());
    }
}
