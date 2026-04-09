package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumItemAstNodeTest {

    private EnumItemAstNode node;

    @BeforeEach
    void setUp() {
        node = new EnumItemAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
