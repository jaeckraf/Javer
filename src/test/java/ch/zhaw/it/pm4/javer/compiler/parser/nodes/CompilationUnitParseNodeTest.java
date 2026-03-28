package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CompilationUnitParseNodeTest {

    private CompilationUnitParseNode node;

    @BeforeEach
    void setUp() {
        node = new CompilationUnitParseNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}

