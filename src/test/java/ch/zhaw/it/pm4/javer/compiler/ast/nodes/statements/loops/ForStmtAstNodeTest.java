package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop.ForStmtAstNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForStmtAstNodeTest {

    private ForStmtAstNode node;

    @BeforeEach
    void setUp() {
        node = new ForStmtAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
