package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructTypeParseNodeTest {

    private StructTypeParseNode node;

    @BeforeEach
    void setUp() {
        node = new StructTypeParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
