package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class VoidTypeParseNodeTest {

    private VoidTypeParseNode node;

    @BeforeEach
    void setUp() {
        node = new VoidTypeParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
