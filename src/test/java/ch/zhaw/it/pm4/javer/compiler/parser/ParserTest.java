package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private DiagnosticBag diagnostics;
    private List<Token> tokens;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticBag();
        tokens = new ArrayList<>();
    }

    @Test
    void parse_WithOnlyEOFToken_ReturnsRootNode() {
        tokens.add(new Token(TokenType.EOF, ""));

        Parser parser = new Parser(tokens, diagnostics);
        CompilationUnitParserNode rootNode = parser.parse();

        assertNotNull(rootNode, "Parser should return a non-null CompilationUnitParserNode.");
    }

    @Test
    void parse_WithMultipleTokens_ConsumesAllTokensUntilEOF() {
        tokens.add(new Token(TokenType.LET, "let"));
        tokens.add(new Token(TokenType.ID, "x"));
        tokens.add(new Token(TokenType.ASSIGN, "="));
        tokens.add(new Token(TokenType.INT_LIT, "5"));
        tokens.add(new Token(TokenType.EOF, ""));

        Parser parser = new Parser(tokens, diagnostics);
        CompilationUnitParserNode rootNode = parser.parse();

        assertNotNull(rootNode, "Parser should return a valid root node.");
    }

    @Test
    @Disabled("To be implemented in future sprint once parseVarDecl() is wired up")
    void parseVarDecl_ValidSyntax_ReturnsVarDeclNode() {
        fail("Implement this test when the parser actively builds variable declaration nodes.");
    }

    @Test
    @Disabled("To be implemented in future sprint to test the consume() method's error reporting")
    void parseVarDecl_MissingIdentifier_ReportsErrorToDiagnosticBag() {
        fail("Implement this test when consume() is used to catch missing variable names.");
    }

    @Test
    @Disabled("To be implemented in future sprint: If Statements")
    void parseIfStmt_ValidSyntax_ReturnsIfStmtNode() {
        fail("Implement this test when parseIfStmt() is fleshed out.");
    }

    @Test
    @Disabled("To be implemented in future sprint: Expression Precedence")
    void parseExpression_AdditionAndMultiplication_HandlesPrecedenceCorrectly() {
        fail("Implement this test when the expression precedence chain is fleshed out.");
    }
}