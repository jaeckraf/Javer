package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class NewExpressionAstNodeTest {

    private NewExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new NewExpressionAstNode(null, null, null);
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
