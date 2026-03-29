package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class SwitchStmtParseNodeTest {

    private SwitchStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new SwitchStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
