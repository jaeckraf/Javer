package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class PostfixParseNodeTest {

    private PostfixParseNode node;

    @BeforeEach
    void setUp() {
        node = new PostfixParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
