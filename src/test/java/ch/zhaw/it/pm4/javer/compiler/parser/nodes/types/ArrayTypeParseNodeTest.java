package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ArrayTypeParseNodeTest {

    private ArrayTypeParseNode node;

    @BeforeEach
    void setUp() {
        node = new ArrayTypeParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
