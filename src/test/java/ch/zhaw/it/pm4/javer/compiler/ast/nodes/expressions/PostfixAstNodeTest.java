package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.postfix.PostfixAstNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class PostfixAstNodeTest {

    private PostfixAstNode node;

    @BeforeEach
    void setUp() {
        node = new PostfixAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
