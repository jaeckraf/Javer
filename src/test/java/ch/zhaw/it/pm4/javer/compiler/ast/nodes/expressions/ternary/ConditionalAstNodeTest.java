package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ternary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ConditionalAstNodeTest {

    private ConditionalAstNode node;

    @BeforeEach
    void setUp() {
        node = new ConditionalAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
