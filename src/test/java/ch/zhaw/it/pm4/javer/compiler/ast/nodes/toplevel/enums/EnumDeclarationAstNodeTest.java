package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumDeclarationAstNodeTest {

    private EnumDeclarationAstNode node;

    @BeforeEach
    void setUp() {
        node = new EnumDeclarationAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
