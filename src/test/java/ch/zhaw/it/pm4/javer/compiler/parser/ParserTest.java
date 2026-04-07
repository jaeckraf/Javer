/*
package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenPosition;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ParserTest {

    private DiagnosticBag diagnostics;
    private List<Token> tokens;
    private TokenPosition position;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticBag();
        tokens = new ArrayList<>();
        position = new TokenPosition(1, 2, 1);
    }

    @Test
    void parse_WithOnlyEOFToken_ReturnsRootNode() {
        tokens.add(new Token(TokenType.SPECIAL_END_OF_FILE, "", position));

        Parser parser = new Parser(tokens, diagnostics);
        CompilationUnitParseNode rootNode = parser.parse();

        assertNotNull(rootNode, "Parser should return a non-null CompilationUnitParserNode.");
    }

    @Test
    void parse_WithMultipleTokens_ConsumesAllTokensUntilEOF() {
        tokens.add(new Token(TokenType.KEYWORD_LET, "let", position));
        tokens.add(new Token(TokenType.ID_IDENTIFIER, "x", position));
        tokens.add(new Token(TokenType.OPERATOR_ASSIGN, "=", position));
        tokens.add(new Token(TokenType.LITERAL_INTEGER, "5", position));
        tokens.add(new Token(TokenType.SPECIAL_END_OF_FILE, "", position));

        Parser parser = new Parser(tokens, diagnostics);
        CompilationUnitParseNode rootNode = parser.parse();

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

    @Test
    @Disabled("Todo")
    public void testParseBinaryExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseArrayInitParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseVarInitParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseCallExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseEnumAccessExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseIndexParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseLiteralConstantParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseNameAccessExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseNewExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseParenthesizedExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseConditionalParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseUnaryExpressionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseAssignmentParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseExpressionListParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParsePostfixParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseBreakStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseContinueStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseReturnStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseCaseClauseParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseDefaultClauseParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseSwitchStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseIfStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseDoWhileStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseForInitParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseForStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseForUpdateParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseWhileStmtParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseBlockParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseExpressionStatementParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseVarDeclarationParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseEnumDeclarationParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseEnumItemParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseFunctionParameterParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseFunctionParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseStructDeclarationParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseStructItemParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseArrayTypeParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseAtomicTypeParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseEnumTypeParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseStructTypeParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseVoidTypeParseNode() {
        fail("Not implemented and tested yet.");
    }

    @Test
    @Disabled("Todo")
    public void testParseParseCompilationUnitParseNode() {
        fail("Not implemented and tested yet.");
    }

}*/
