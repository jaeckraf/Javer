package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ArrayInitParseNodeTest {

    private ArrayInitParseNode node;

    @BeforeEach
    void setUp() {
        node = new ArrayInitParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
