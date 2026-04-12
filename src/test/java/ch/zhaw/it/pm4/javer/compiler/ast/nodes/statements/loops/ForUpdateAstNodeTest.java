package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForUpdateAstNodeTest {

    private ForUpdateAstNode node;

    @BeforeEach
    void setUp() {
        node = new ForUpdateAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
