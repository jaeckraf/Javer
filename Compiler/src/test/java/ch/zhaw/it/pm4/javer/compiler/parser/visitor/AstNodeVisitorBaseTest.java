package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitorBase;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class AstNodeVisitorBaseTest {

    @Test
    void visitDefault_returnsEmptyOptional() {
        TestBridge visitor = mock(TestBridge.class, CALLS_REAL_METHODS);
        AstNode node = mock(AstNode.class);

        Optional<String> result = visitor.callVisitDefault(node);

        assertTrue(result.isEmpty());
    }

    private abstract static class TestBridge extends AstNodeVisitorBase<String> {
        public Optional<String> callVisitDefault(AstNode node) {
            return visitDefault(node);
        }
    }
}