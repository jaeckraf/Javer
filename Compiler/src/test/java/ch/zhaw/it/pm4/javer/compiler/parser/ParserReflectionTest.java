package ch.zhaw.it.pm4.javer.compiler.parser;
/*
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParserReflectionTest {

    private DiagnosticBag diagnostics;

    @BeforeEach
    void setUp() {
        diagnostics = mock(DiagnosticBag.class);
    }

    // ----------------------------------------------------
    // Helper zum Erzeugen eines Parsers
    // ----------------------------------------------------
    private Parser createParser(Token... tokens) {
        // An deinen echten Konstruktor anpassen
        return new Parser(List.of(tokens), diagnostics);
    }

    private Token token(TokenType type) {
        return new Token(type, "", new SourceLocation(1, 1, 1));
    }

    // ----------------------------------------------------
    // Reflection-Helper
    // ----------------------------------------------------
    private Object invokePrivate(Parser parser, String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = Parser.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(parser, args);
    }

    private Object invokePrivate(Parser parser, String methodName) throws Exception {
        return invokePrivate(parser, methodName, new Class<?>[]{});
    }

    private Object invokePrivate(Parser parser, String methodName, Class<?> paramType, Object arg) throws Exception {
        return invokePrivate(parser, methodName, new Class<?>[]{paramType}, arg);
    }

    private Object readPrivateField(Object target, String fieldName) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    // ----------------------------------------------------
    // Tests für peek(int)
    // ----------------------------------------------------
    @Nested
    class PeekTests {

        @Test
        @DisplayName("peek(0) liefert aktuelles Token")
        void peek_zero_returnsCurrentToken() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "peek", int.class, 0);

            assertEquals(TokenType.ID_IDENTIFIER, result.getTokenType());
        }

        @Test
        @DisplayName("peek(1) liefert nächstes Token")
        void peek_one_returnsNextToken() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "peek", int.class, 1);

            assertEquals(TokenType.OPERATOR_PLUS, result.getTokenType());
        }

        @Test
        @DisplayName("peek(großer Offset) liefert EOF")
        void peek_largeOffset_returnsEof() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "peek", int.class, 999);

            assertEquals(TokenType.SPECIAL_END_OF_FILE, result.getTokenType());
        }

        @Test
        @DisplayName("peek(negativer Offset) wird auf 0 korrigiert")
        void peek_negativeOffset_clampsToZero() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "peek", int.class, -1);

            assertEquals(TokenType.ID_IDENTIFIER, result.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests für currentToken()
    // ----------------------------------------------------
    @Nested
    class CurrentTokenTests {

        @Test
        void currentToken_returnsCurrentToken() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "currentToken");

            assertEquals(TokenType.ID_IDENTIFIER, result.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests für lookahead()
    // ----------------------------------------------------
    @Nested
    class LookaheadTests {

        @Test
        void lookahead_returnsNextToken() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.OPERATOR_MINUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Token result = (Token) invokePrivate(parser, "lookahead");

            assertEquals(TokenType.OPERATOR_MINUS, result.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests für matchCurrentToken(TokenType)
    // ----------------------------------------------------
    @Nested
    class MatchCurrentTokenTests {

        @Test
        void matchCurrentToken_returnsTrue_onMatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "matchCurrentToken", TokenType.class, TokenType.LITERAL_INTEGER);

            assertTrue(result);
        }

        @Test
        void matchCurrentToken_returnsFalse_onMismatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "matchCurrentToken", TokenType.class, TokenType.LITERAL_BINARY);

            assertFalse(result);
        }
    }

    // ----------------------------------------------------
    // Tests für matchNextToken(TokenType)
    // ----------------------------------------------------
    @Nested
    class MatchNextTokenTests {

        @Test
        void matchNextToken_returnsTrue_onMatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "matchNextToken", TokenType.class, TokenType.OPERATOR_PLUS);

            assertTrue(result);
        }

        @Test
        void matchNextToken_returnsFalse_onMismatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "matchNextToken", TokenType.class, TokenType.SYMBOL_SEMICOLON);

            assertFalse(result);
        }
    }

    // ----------------------------------------------------
    // Tests für matchAnyCurrentToken(TokenType...)
    // Achtung: Varargs via Reflection als Array übergeben
    // ----------------------------------------------------
    @Nested
    class MatchAnyCurrentTokenTests {

        @Test
        void matchAnyCurrentToken_returnsTrue_ifAnyMatches() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(
                    parser,
                    "matchAnyCurrentToken",
                    new Class<?>[]{TokenType[].class},
                    (Object) new TokenType[]{TokenType.OPERATOR_PLUS, TokenType.LITERAL_INTEGER, TokenType.OPERATOR_MINUS}
            );

            assertTrue(result);
        }

        @Test
        void matchAnyCurrentToken_returnsFalse_ifNoneMatches() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(
                    parser,
                    "matchAnyCurrentToken",
                    new Class<?>[]{TokenType[].class},
                    (Object) new TokenType[]{TokenType.OPERATOR_PLUS, TokenType.OPERATOR_MINUS}
            );

            assertFalse(result);
        }
    }

    // ----------------------------------------------------
    // Tests für matchAnyNextToken(TokenType...)
    // ----------------------------------------------------
    @Nested
    class MatchAnyNextTokenTests {

        @Test
        void matchAnyNextToken_returnsTrue_ifAnyMatches() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(
                    parser,
                    "matchAnyNextToken",
                    new Class<?>[]{TokenType[].class},
                    (Object) new TokenType[]{TokenType.OPERATOR_MINUS, TokenType.OPERATOR_PLUS}
            );

            assertTrue(result);
        }

        @Test
        void matchAnyNextToken_returnsFalse_ifNoneMatches() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(
                    parser,
                    "matchAnyNextToken",
                    new Class<?>[]{TokenType[].class},
                    (Object) new TokenType[]{TokenType.OPERATOR_MULTIPLY, TokenType.OPERATOR_DIVIDE}
            );

            assertFalse(result);
        }
    }

    // ----------------------------------------------------
    // Tests für consumeToken()
    // ----------------------------------------------------
    @Nested
    class ConsumeTokenTests {

        @Test
        void consumeToken_advancesToNextToken() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.OPERATOR_PLUS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            invokePrivate(parser, "consumeToken");

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.OPERATOR_PLUS, current.getTokenType());
        }

        @Test
        void consumeToken_atEnd_keepsParserStable() throws Exception {
            Parser parser = createParser(
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            invokePrivate(parser, "consumeToken");
            invokePrivate(parser, "consumeToken");
            invokePrivate(parser, "consumeToken");

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SPECIAL_END_OF_FILE, current.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests für expectTokenType(TokenType)
    // ----------------------------------------------------
    @Nested
    @Disabled("DiagnosticBag is not yet implemented inside the parser")
    class ExpectTokenTypeTests {

        @Test
        void expectTokenType_returnsTrue_onMatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "expectTokenType", TokenType.class, TokenType.LITERAL_INTEGER);

            assertTrue(result);
            verifyNoInteractions(diagnostics);
        }

        @Test
        void expectTokenType_returnsFalse_onMismatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            boolean result = (boolean) invokePrivate(parser, "expectTokenType", TokenType.class, TokenType.OPERATOR_PLUS);

            assertFalse(result);
        }

        @Test
        void expectTokenType_reportsDiagnostic_onMismatch() throws Exception {
            Parser parser = createParser(
                    token(TokenType.LITERAL_INTEGER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            invokePrivate(parser, "expectTokenType", TokenType.class, TokenType.OPERATOR_PLUS);

            verify(diagnostics).add(any(SourceLocation.class), any(), contains("Expected token"));
        }
    }

    // ----------------------------------------------------
    // Tests für parseFunctionDeclaration()
    // ----------------------------------------------------
    @Nested
    class ParseFunctionDeclarationTests {

        @Test
        void parseFunctionDeclaration_mapsAllCoreParts() throws Exception {
            Parser parser = createParser(
                    token(TokenType.KEYWORD_FUNCTION),
                    token(TokenType.TYPE_VOID),
                    new Token(TokenType.ID_IDENTIFIER, "main", new SourceLocation(1, 1, 1)),
                    token(TokenType.SYMBOL_LEFT_PARENTHESIS),
                    token(TokenType.TYPE_INTEGER),
                    new Token(TokenType.ID_IDENTIFIER, "count", new SourceLocation(1, 1, 1)),
                    token(TokenType.SYMBOL_COMMA),
                    token(TokenType.TYPE_STRING),
                    new Token(TokenType.ID_IDENTIFIER, "name", new SourceLocation(1, 1, 1)),
                    token(TokenType.SYMBOL_RIGHT_PARENTHESIS),
                    token(TokenType.SYMBOL_LEFT_BRACE),
                    token(TokenType.SYMBOL_RIGHT_BRACE),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            @SuppressWarnings("unchecked")
            Optional<Object> parsedFunction = (Optional<Object>) invokePrivate(parser, "parseFunctionDeclaration");

            assertTrue(parsedFunction.isPresent());
            Object function = parsedFunction.get();
            assertEquals("FunctionDeclaration", function.getClass().getSimpleName());
            assertEquals("main", readPrivateField(function, "name"));
            assertEquals("VoidType", readPrivateField(function, "returnType").getClass().getSimpleName());

            @SuppressWarnings("unchecked")
            List<Object> parameters = (List<Object>) readPrivateField(function, "parameters");
            assertEquals(2, parameters.size());
            assertEquals("count", readPrivateField(parameters.get(0), "name"));
            assertEquals("name", readPrivateField(parameters.get(1), "name"));

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SPECIAL_END_OF_FILE, current.getTokenType());
        }

        @Test
        void parseFunctionDeclaration_missingFunctionName_returnsEmptyOptional() throws Exception {
            Parser parser = createParser(
                    token(TokenType.KEYWORD_FUNCTION),
                    new Token(TokenType.ID_IDENTIFIER, "main", new SourceLocation(1, 1, 1)),
                    token(TokenType.SYMBOL_LEFT_PARENTHESIS),
                    token(TokenType.SYMBOL_RIGHT_PARENTHESIS),
                    token(TokenType.SYMBOL_LEFT_BRACE),
                    token(TokenType.SYMBOL_RIGHT_BRACE),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            @SuppressWarnings("unchecked")
            Optional<Object> parsedFunction = (Optional<Object>) invokePrivate(parser, "parseFunctionDeclaration");

            assertTrue(parsedFunction.isEmpty());

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SYMBOL_LEFT_PARENTHESIS, current.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests fuer parsePrimitiveType()
    // ----------------------------------------------------
    @Nested
    class ParsePrimitiveTypeTests {

        @Test
        void parsePrimitiveType_invalidToken_reportsDiagnosticAndReturnsInvalidType() throws Exception {
            Parser parser = createParser(
                    token(TokenType.SYMBOL_LEFT_PARENTHESIS),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            PrimitiveType parsedType = (PrimitiveType) invokePrivate(parser, "parsePrimitiveType");

            assertEquals(PrimitiveTypeKind.INVALID, parsedType.getKind());
            verify(diagnostics).add(any(SourceLocation.class), eq(Severity.ERROR), contains("Invalid primitive type."));

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SYMBOL_LEFT_PARENTHESIS, current.getTokenType());
        }
    }

    // ----------------------------------------------------
    // Tests fuer parseBlock() / parseStatement()
    // ----------------------------------------------------
    @Nested
    class ParseBlockAndStatementTests {

        @Test
        void parseBlock_missingLeftBrace_reportsDiagnosticAndReturnsEmptyBlock() throws Exception {
            Parser parser = createParser(
                    token(TokenType.ID_IDENTIFIER),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Object block = invokePrivate(parser, "parseBlock");

            assertEquals("BlockStatement", block.getClass().getSimpleName());
            @SuppressWarnings("unchecked")
            List<Object> statements = (List<Object>) readPrivateField(block, "statements");
            assertTrue(statements.isEmpty());
            verify(diagnostics).add(any(SourceLocation.class), eq(Severity.ERROR), contains("Block statement must start with '{'."));

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.ID_IDENTIFIER, current.getTokenType());
        }

        @Test
        void parseStatement_leftBrace_dispatchesToParseBlock() throws Exception {
            Parser parser = createParser(
                    token(TokenType.SYMBOL_LEFT_BRACE),
                    token(TokenType.SYMBOL_RIGHT_BRACE),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Object statement = invokePrivate(parser, "parseStatement");

            assertEquals("BlockStatement", statement.getClass().getSimpleName());

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SPECIAL_END_OF_FILE, current.getTokenType());
        }

        @Test
        void parseBlock_nestedBlock_collectsInnerStatement() throws Exception {
            Parser parser = createParser(
                    token(TokenType.SYMBOL_LEFT_BRACE),
                    token(TokenType.SYMBOL_LEFT_BRACE),
                    token(TokenType.SYMBOL_RIGHT_BRACE),
                    token(TokenType.SYMBOL_RIGHT_BRACE),
                    token(TokenType.SPECIAL_END_OF_FILE)
            );

            Object block = invokePrivate(parser, "parseBlock");

            assertEquals("BlockStatement", block.getClass().getSimpleName());

            @SuppressWarnings("unchecked")
            List<Object> statements = (List<Object>) readPrivateField(block, "statements");
            assertEquals(1, statements.size());
            assertEquals("BlockStatement", statements.getFirst().getClass().getSimpleName());

            Token current = (Token) invokePrivate(parser, "currentToken");
            assertEquals(TokenType.SPECIAL_END_OF_FILE, current.getTokenType());
        }
    }

}*/