package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilationUnitParseNodeTest {

    private CompilationUnitParseNode node;
    private MockParseNodeVisitor visitor;

    @BeforeEach
    void setUp() {
        node = new CompilationUnitParseNode();
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
        assertTrue(result, "accept() should return true from visitor");
    }

    @Test
    void testNodeCreation() {
        assertNotNull(node, "Node should not be null");
    }
}

