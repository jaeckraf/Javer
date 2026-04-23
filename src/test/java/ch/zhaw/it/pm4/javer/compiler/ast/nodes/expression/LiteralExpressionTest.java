package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LiteralExpressionTest {

    @Test
    public void testGetters() {
        LiteralExpression<Integer> exp = new LiteralExpression<>(LiteralKind.INT, 42);
        assertEquals(LiteralKind.INT, exp.getKind());
        assertEquals(42, exp.getValue());
    }

    @Test
    public void testEqualsAndHashCode() {
        LiteralExpression<Integer> exp1 = new LiteralExpression<>(LiteralKind.INT, 42);
        LiteralExpression<Integer> exp2 = new LiteralExpression<>(LiteralKind.INT, 42);
        LiteralExpression<Integer> exp3 = new LiteralExpression<>(LiteralKind.INT, 43);
        LiteralExpression<Double> exp4 = new LiteralExpression<>(LiteralKind.DOUBLE, 42.0);

        assertEquals(exp1, exp2);
        assertEquals(exp1.hashCode(), exp2.hashCode());

        assertNotEquals(exp1, exp3);
        assertNotEquals(exp1, exp4);
        assertNotEquals(exp1, null);
        assertNotEquals(exp1, new Object());
    }

    @Test
    public void testAccept() {
        LiteralExpression<Integer> exp = new LiteralExpression<>(LiteralKind.INT, 42);
        AstNodeVisitor<Void> mockVisitor = mock(AstNodeVisitor.class);
        
        exp.accept(mockVisitor);
        
        verify(mockVisitor).visit(exp);
    }
}

