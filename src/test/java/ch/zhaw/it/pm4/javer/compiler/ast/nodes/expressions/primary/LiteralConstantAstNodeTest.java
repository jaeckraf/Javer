package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.literal.LiteralConstantAstNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class LiteralConstantAstNodeTest {

    private LiteralConstantAstNode node;

    @BeforeEach
    void setUp() {
        node = new LiteralConstantAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
