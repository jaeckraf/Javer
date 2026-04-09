package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ArrayInitAstNodeTest {

    private ArrayInitAstNode node;

    @BeforeEach
    void setUp() {
        node = new ArrayInitAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
