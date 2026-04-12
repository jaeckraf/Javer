package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CaseClauseAstNodeTest {

    private CaseClauseAstNode node;

    @BeforeEach
    void setUp() {
        node = new CaseClauseAstNode();
    }

    @AfterEach
    void tearDown() {
        node = null;
    }

}
