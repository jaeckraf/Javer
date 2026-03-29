package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DoWhileStmtParseNodeTest {

    private DoWhileStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new DoWhileStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
