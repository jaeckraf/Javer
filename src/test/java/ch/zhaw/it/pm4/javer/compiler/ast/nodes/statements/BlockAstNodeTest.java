package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class BlockAstNodeTest {

    private BlockAstNode node;

    @BeforeEach
    void setUp() {
        node = new BlockAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
