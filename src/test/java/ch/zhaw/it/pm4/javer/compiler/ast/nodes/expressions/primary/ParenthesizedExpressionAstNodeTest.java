package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ParenthesizedExpressionAstNodeTest {

    private ParenthesizedExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new ParenthesizedExpressionAstNode(null);
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
