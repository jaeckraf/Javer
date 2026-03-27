package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForStmtParseNodeTest {

    private ForStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new ForStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
