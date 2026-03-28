package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class ForInitParseNodeTest {

    private ForInitParseNode node;

    @BeforeEach
    void setUp() {
        node = new ForInitParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
