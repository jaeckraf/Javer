package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class AtomicTypeAstNodeTest {

    private AtomicTypeAstNode node;

    @BeforeEach
    void setUp() {
        node = new AtomicTypeAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
