package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class VarDeclarationAstNodeTest {

    private VarDeclarationAstNode node;

    @BeforeEach
    void setUp() {
        node = new VarDeclarationAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
