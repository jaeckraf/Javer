package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CaseClauseParseNodeTest {

    private CaseClauseParseNode node;

    @BeforeEach
    void setUp() {
        node = new CaseClauseParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
