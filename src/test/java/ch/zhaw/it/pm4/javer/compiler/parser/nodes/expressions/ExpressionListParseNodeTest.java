package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ExpressionListParseNodeTest {

    private ExpressionListParseNode node;

    @BeforeEach
    void setUp() {
        node = new ExpressionListParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
