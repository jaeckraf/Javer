package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop.ForInitAstNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForInitAstNodeTest {

    private ForInitAstNode node;

    @BeforeEach
    void setUp() {
        node = new ForInitAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
