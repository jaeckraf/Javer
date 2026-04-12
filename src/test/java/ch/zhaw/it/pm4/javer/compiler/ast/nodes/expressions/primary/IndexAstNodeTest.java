package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class IndexAstNodeTest {

    private IndexAstNode node;

    @BeforeEach
    void setUp() {
        node = new IndexAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
