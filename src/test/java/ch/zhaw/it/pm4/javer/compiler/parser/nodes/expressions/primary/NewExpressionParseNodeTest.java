package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class NewExpressionParseNodeTest {

    private NewExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new NewExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
