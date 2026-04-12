package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CallExpressionAstNodeTest {

    private CallExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new CallExpressionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
