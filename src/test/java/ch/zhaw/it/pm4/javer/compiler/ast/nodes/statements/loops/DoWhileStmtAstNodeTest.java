package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DoWhileStmtAstNodeTest {

    private DoWhileStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new DoWhileStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
