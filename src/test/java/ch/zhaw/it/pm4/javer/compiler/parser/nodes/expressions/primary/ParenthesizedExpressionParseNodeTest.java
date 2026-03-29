package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ParenthesizedExpressionParseNodeTest {

    private ParenthesizedExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new ParenthesizedExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
