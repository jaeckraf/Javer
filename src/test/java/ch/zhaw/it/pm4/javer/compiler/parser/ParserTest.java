package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenPosition;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import ch.zhaw.it.pm4.javer.compiler.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testBinaryExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testArrayInitParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testVarInitParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testCallExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testEnumAccessExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testIndexParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testLiteralConstantParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testNameAccessExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testParenthesizedExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testConditionalParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testUnaryExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testAssignmentParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testExpressionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testPostfixParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testBreakStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testContinueStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testReturnStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testCaseClauseParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testDefaultClauseParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testSwitchStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testIfStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testDoWhileStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testForInitParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testForStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testForUpdateParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testWhileStmtParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testBlockParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testExpressionStatementParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testVarDeclarationParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testEnumDeclarationParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testEnumItemParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testFunctionParameterParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testFunctionParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testStructDeclarationParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testStructItemParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testArrayTypeParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testAtomicTypeParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testEnumTypeParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testStructTypeParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testVoidTypeParseNode() { fail("Not implemented and tested yet."); }
    @Test
    @Disabled("Todo")
    public void testCompilationUnitParseNode() { fail("Not implemented and tested yet."); }

}