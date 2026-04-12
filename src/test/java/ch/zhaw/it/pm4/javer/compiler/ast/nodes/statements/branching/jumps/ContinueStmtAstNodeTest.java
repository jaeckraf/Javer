package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ContinueStmtAstNodeTest {

    private ContinueStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new ContinueStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
