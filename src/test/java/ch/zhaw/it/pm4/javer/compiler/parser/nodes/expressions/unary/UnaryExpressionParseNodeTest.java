package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.unary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class UnaryExpressionParseNodeTest {

    private UnaryExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new UnaryExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
