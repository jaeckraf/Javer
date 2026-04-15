package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CompilationUnitAstNodeTest {

    private CompilationUnit node;

    @BeforeEach
    void setUp() {
        node = new CompilationUnit();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}

