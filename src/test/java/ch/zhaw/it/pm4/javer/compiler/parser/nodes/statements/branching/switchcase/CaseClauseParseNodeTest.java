package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.MockParseNodeVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseClauseParseNodeTest {

    private CaseClauseParseNode node;
    private MockParseNodeVisitor visitor;

    @BeforeEach
    void setUp() {
        node = new CaseClauseParseNode();
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
