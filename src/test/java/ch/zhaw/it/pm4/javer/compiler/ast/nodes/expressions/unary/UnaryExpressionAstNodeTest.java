package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.unary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class UnaryExpressionAstNodeTest {

    private UnaryExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new UnaryExpressionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
