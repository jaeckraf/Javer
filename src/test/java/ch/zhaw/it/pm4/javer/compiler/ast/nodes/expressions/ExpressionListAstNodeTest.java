package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ExpressionListAstNodeTest {

    private ExpressionListAstNode node;

    @BeforeEach
    void setUp() {
        node = new ExpressionListAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
