package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class NameAccessExpressionAstNodeTest {

    private NameAccessExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new NameAccessExpressionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
