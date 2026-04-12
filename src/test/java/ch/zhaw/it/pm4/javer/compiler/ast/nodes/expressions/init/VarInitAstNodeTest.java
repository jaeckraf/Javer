package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class VarInitAstNodeTest {

    private VarInitAstNode node;

    @BeforeEach
    void setUp() {
        node = new VarInitAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
