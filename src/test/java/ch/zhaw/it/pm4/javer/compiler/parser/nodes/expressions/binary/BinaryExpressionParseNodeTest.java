package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.binary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class BinaryExpressionParseNodeTest {

    private BinaryExpressionParseNode node;

    @BeforeEach
    void setUp() {
        node = new BinaryExpressionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}