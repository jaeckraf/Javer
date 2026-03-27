package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.MockParseNodeVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumDeclarationParseNodeTest {

    private EnumDeclarationParseNode node;
    private MockParseNodeVisitor visitor;

    @BeforeEach
    void setUp() {
        node = new EnumDeclarationParseNode();
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
