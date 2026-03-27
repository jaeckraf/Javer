package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class WhileStmtParseNodeTest {

    private WhileStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new WhileStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
