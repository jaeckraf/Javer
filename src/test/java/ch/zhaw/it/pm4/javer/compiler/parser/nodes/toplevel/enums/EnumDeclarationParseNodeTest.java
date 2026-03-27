package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class EnumDeclarationParseNodeTest {

    private EnumDeclarationParseNode node;

    @BeforeEach
    void setUp() {
        node = new EnumDeclarationParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
