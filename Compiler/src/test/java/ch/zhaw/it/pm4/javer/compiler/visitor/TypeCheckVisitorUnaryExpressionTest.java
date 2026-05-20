package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TypeCheckVisitorUnaryExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    private UnaryExpression unary(UnaryExpressionKind kind, LiteralExpression<?> operand, PrimitiveTypeInfo type) {
        operand.setResultingType(type);
        return new UnaryExpression(kind, operand);
    }

    @Test
    void testLogicalNotWithBoolean() {
        var expr = unary(UnaryExpressionKind.LOGICAL_NOT, new LiteralExpression<>(LiteralKind.BOOLEAN, true), PrimitiveTypeInfo.BOOL);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.BOOL, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testLogicalNotWithInt() {
        var expr = unary(UnaryExpressionKind.LOGICAL_NOT, new LiteralExpression<>(LiteralKind.INT, 1), PrimitiveTypeInfo.INT);
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testBitwiseNotWithInt() {
        var expr = unary(UnaryExpressionKind.BITWISE_NOT, new LiteralExpression<>(LiteralKind.INT, 1), PrimitiveTypeInfo.INT);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testBitwiseNotWithDouble() {
        var expr = unary(UnaryExpressionKind.BITWISE_NOT, new LiteralExpression<>(LiteralKind.DOUBLE, 1.0), PrimitiveTypeInfo.DOUBLE);
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testMinusWithInt() {
        var expr = unary(UnaryExpressionKind.MINUS, new LiteralExpression<>(LiteralKind.INT, 1), PrimitiveTypeInfo.INT);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testMinusWithDouble() {
        var expr = unary(UnaryExpressionKind.MINUS, new LiteralExpression<>(LiteralKind.DOUBLE, 1.0), PrimitiveTypeInfo.DOUBLE);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testMinusWithBool() {
        var expr = unary(UnaryExpressionKind.MINUS, new LiteralExpression<>(LiteralKind.BOOLEAN, true), PrimitiveTypeInfo.BOOL);
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testPreIncrementWithIntVariable() {
        var varExpr = mock(NameExpression.class);
        when(varExpr.getResultingType()).thenReturn(PrimitiveTypeInfo.INT);

        var expr = new UnaryExpression(UnaryExpressionKind.PRE_INCREMENT, varExpr);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testPreDecrementWithIntVariable() {
        var varExpr = mock(NameExpression.class);
        when(varExpr.getResultingType()).thenReturn(PrimitiveTypeInfo.INT);

        var expr = new UnaryExpression(UnaryExpressionKind.PRE_DECREMENT, varExpr);
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }


    @Test
    void testPreIncrementWithBool() {
        var expr = unary(UnaryExpressionKind.PRE_INCREMENT, new LiteralExpression<>(LiteralKind.BOOLEAN, true), PrimitiveTypeInfo.BOOL);
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }
}