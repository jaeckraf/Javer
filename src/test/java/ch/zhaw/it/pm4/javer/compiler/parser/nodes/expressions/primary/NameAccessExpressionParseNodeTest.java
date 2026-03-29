package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class NameAccessExpressionParseNodeTest {

    private NameAccessExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new NameAccessExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
