package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructItemAstNodeTest {

    private StructItemAstNode node;

    @BeforeEach
    void setUp() {
        node = new StructItemAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
