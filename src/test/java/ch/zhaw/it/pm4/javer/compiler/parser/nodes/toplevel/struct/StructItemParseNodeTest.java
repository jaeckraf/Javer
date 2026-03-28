package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructItemParseNodeTest {

    private StructItemParseNode node;

    @BeforeEach
    void setUp() {
        node = new StructItemParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
