package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumTypeParseNodeTest {

    private EnumTypeParseNode node;

    @BeforeEach
    void setUp() {
        node = new EnumTypeParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
