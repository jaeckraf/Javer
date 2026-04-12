package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.binary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class BinaryExpressionAstNodeTest {

    private BinaryExpressionAstNode node;

    @BeforeEach
    void setUp() {
        node = new BinaryExpressionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}