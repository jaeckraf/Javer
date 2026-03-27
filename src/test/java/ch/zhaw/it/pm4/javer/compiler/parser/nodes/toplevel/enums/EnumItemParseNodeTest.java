package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumItemParseNodeTest {

    private EnumItemParseNode node;

    @BeforeEach
    void setUp() {
        node = new EnumItemParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
