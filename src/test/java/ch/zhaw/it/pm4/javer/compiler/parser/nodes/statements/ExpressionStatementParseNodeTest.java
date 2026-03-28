package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ExpressionStatementParseNodeTest {

    private ExpressionStatementParseNode node;

    @BeforeEach
    void setUp() {
        node = new ExpressionStatementParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
