package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForUpdateParseNodeTest {

    private ForUpdateParseNode node;

    @BeforeEach
    void setUp() {
        node = new ForUpdateParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
