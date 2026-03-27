package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DefaultClauseParseNodeTest {

    private DefaultClauseParseNode node;

    @BeforeEach
    void setUp() {
        node = new DefaultClauseParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
