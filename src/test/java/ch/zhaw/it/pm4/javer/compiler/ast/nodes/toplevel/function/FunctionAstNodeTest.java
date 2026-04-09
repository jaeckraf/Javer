package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class FunctionAstNodeTest {

    private FunctionAstNode node;

    @BeforeEach
    void setUp() {
        node = new FunctionAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
