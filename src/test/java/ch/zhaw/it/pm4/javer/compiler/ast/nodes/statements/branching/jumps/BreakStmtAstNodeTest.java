package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class BreakStmtAstNodeTest {

    private BreakStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new BreakStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
