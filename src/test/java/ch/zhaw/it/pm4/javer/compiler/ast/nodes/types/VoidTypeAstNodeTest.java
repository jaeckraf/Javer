package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class VoidTypeAstNodeTest {

    private VoidTypeAstNode node;

    @BeforeEach
    void setUp() {
        node = new VoidTypeAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
