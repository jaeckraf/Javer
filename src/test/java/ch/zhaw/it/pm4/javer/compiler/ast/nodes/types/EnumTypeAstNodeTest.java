package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumTypeAstNodeTest {

    private EnumTypeAstNode node;

    @BeforeEach
    void setUp() {
        node = new EnumTypeAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
