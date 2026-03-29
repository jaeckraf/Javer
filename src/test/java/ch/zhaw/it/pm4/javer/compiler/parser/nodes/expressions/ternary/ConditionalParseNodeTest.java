package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ternary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ConditionalParseNodeTest {

    private ConditionalParseNode node;

    @BeforeEach
    void setUp() {
        node = new ConditionalParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
