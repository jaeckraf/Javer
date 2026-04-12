package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class SwitchStmtAstNodeTest {

    private SwitchStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new SwitchStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
