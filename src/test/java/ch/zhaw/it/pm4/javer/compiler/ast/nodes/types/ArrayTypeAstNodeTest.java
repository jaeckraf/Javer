package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ArrayTypeAstNodeTest {

    private ArrayTypeAstNode node;

    @BeforeEach
    void setUp() {
        node = new ArrayTypeAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
