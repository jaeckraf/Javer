package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class LiteralConstantParseNodeTest {

    private LiteralConstantParseNode node;

    @BeforeEach
    void setUp() {
        node = new LiteralConstantParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
