package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class FunctionParseNodeTest {

    private FunctionParseNode node;

    @BeforeEach
    void setUp() {
        node = new FunctionParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
