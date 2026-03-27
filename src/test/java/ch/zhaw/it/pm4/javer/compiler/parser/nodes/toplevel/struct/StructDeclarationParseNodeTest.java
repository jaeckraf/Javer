package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class StructDeclarationParseNodeTest {

    private StructDeclarationParseNode node;

    @BeforeEach
    void setUp() {
        node = new StructDeclarationParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
