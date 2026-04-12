package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnitAstNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CompilationUnitAstNodeTest {

    private CompilationUnitAstNode node;

    @BeforeEach
    void setUp() {
        node = new CompilationUnitAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}

