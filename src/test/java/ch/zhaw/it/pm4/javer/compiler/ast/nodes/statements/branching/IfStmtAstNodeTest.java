package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class IfStmtAstNodeTest {

    private IfStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new IfStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
