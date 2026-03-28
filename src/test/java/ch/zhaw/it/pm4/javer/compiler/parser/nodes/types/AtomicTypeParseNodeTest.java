package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class AtomicTypeParseNodeTest {

    private AtomicTypeParseNode node;

    @BeforeEach
    void setUp() {
        node = new AtomicTypeParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
