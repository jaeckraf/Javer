package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumAccessExpressionAstNodeTest {

    private EnumAccessExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new EnumAccessExpressionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
