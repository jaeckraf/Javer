package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CallExpressionParseNodeTest {

    private CallExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new CallExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
