package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class AssignmentAstNodeTest {

    private AssignmentAstNode node;

    @BeforeEach
    void setUp() {
        node = new AssignmentAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
