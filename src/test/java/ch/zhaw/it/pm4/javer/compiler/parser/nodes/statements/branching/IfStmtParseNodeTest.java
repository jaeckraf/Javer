package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class IfStmtParseNodeTest {

    private IfStmtParseNode node;

    @BeforeEach
    void setUp() {
        node = new IfStmtParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
