package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ReturnStmtAstNodeTest {

    private ReturnStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new ReturnStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
