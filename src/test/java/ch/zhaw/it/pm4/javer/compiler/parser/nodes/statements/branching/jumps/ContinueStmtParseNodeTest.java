package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ContinueStmtParseNodeTest {

    private ContinueStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new ContinueStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
