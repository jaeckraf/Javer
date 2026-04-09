package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class FunctionParameterAstNodeTest {

    private FunctionParameterAstNode node;

    @BeforeEach
    void setUp() {
        node = new FunctionParameterAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
