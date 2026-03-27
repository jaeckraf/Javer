package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.MockParseNodeVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallExpressionParseNodeTest {

    private CallExpressionParseNode node;
    private MockParseNodeVisitor visitor;

    @BeforeEach
    void setUp() {
        node = new CallExpressionParseNode();
        visitor = new MockParseNodeVisitor();
    }

    @AfterEach
    void tearDown() {
        node = null;
        visitor = null;
    }

    @Test
    void testAcceptVisitor() {
        Boolean result = node.accept(visitor);
        assertTrue(result);
    }
}
