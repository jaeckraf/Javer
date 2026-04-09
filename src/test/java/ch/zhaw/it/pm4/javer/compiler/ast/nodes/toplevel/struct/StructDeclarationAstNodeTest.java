package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructDeclarationAstNodeTest {

    private StructDeclarationAstNode node;

    @BeforeEach
    void setUp() {
        node = new StructDeclarationAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
