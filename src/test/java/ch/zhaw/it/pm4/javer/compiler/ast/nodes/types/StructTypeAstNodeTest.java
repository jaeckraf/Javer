package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructTypeAstNodeTest {

    private StructTypeAstNode node;

    @BeforeEach
    void setUp() {
        node = new StructTypeAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
