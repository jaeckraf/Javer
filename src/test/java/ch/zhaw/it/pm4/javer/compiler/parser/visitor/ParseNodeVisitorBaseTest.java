package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class ParseNodeVisitorBaseTest {

    @Test
    void visitDefault_returnsEmptyOptional() {
        TestBridge visitor = mock(TestBridge.class, CALLS_REAL_METHODS);
        ParseNode node = mock(ParseNode.class);

        Optional<String> result = visitor.callVisitDefault(node);

        assertTrue(result.isEmpty());
    }

    private abstract static class TestBridge extends ParseNodeVisitorBase<String> {
        public Optional<String> callVisitDefault(ParseNode node) {
            return visitDefault(node);
        }
    }
}