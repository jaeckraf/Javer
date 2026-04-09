package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ExpressionStatementAstNodeTest {

    private ExpressionStatementAstNode node;

    @BeforeEach
    void setUp() {
        node = new ExpressionStatementAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
