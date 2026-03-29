package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class VarInitParseNodeTest {

    private VarInitParseNode node;

    @BeforeEach
    void setUp() {
        node = new VarInitParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
