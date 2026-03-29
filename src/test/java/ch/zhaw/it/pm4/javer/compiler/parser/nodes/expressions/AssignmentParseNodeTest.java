package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class AssignmentParseNodeTest {

    private AssignmentParseNode node;

    @BeforeEach
    void setUp() {
        node = new AssignmentParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
