package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class WhileStmtAstNodeTest {

    private WhileStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new WhileStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
