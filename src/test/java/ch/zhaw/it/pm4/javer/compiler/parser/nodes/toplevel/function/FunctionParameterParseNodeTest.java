package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class FunctionParameterParseNodeTest {

    private FunctionParameterParseNode node;

    @BeforeEach
    void setUp() {
        node = new FunctionParameterParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
