package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IndexExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeCheckVisitorIndexExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    // --- Positive: Array of int, index is int ---
    @Test
    void testArrayIndexWithInt() {
        var arrayVar = mock(NameExpression.class);
        when(arrayVar.getResultingType()).thenReturn(new ArrayTypeInfo(PrimitiveTypeInfo.INT));

        var indexExpr = new LiteralExpression<>(LiteralKind.INT, 0);
        indexExpr.setResultingType(PrimitiveTypeInfo.INT);

        var expr = new IndexExpression(arrayVar, indexExpr);
        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Positive: Array of double, index is int ---
    @Test
    void testArrayOfDoubleIndexWithInt() {
        var arrayVar = mock(NameExpression.class);
        when(arrayVar.getResultingType()).thenReturn(new ArrayTypeInfo(PrimitiveTypeInfo.DOUBLE));

        var indexExpr = new LiteralExpression<>(LiteralKind.INT, 1);
        indexExpr.setResultingType(PrimitiveTypeInfo.INT);

        var expr = new IndexExpression(arrayVar, indexExpr);
        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Negative: Target is not array ---
    @Test
    void testIndexingNonArray() {
        var notArray = mock(NameExpression.class);
        when(notArray.getResultingType()).thenReturn(PrimitiveTypeInfo.INT);

        var indexExpr = new LiteralExpression<>(LiteralKind.INT, 0);
        indexExpr.setResultingType(PrimitiveTypeInfo.INT);

        var expr = new IndexExpression(notArray, indexExpr);
        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: Index is not int ---
    @Test
    void testIndexWithNonInt() {
        var arrayVar = mock(NameExpression.class);
        when(arrayVar.getResultingType()).thenReturn(new ArrayTypeInfo(PrimitiveTypeInfo.INT));

        var indexExpr = new LiteralExpression<>(LiteralKind.DOUBLE, 1.5);
        indexExpr.setResultingType(PrimitiveTypeInfo.DOUBLE);

        var expr = new IndexExpression(arrayVar, indexExpr);
        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: Index is string ---
    @Test
    void testIndexWithString() {
        var arrayVar = mock(NameExpression.class);
        when(arrayVar.getResultingType()).thenReturn(new ArrayTypeInfo(PrimitiveTypeInfo.INT));

        var indexExpr = new LiteralExpression<>(LiteralKind.STRING, "foo");
        indexExpr.setResultingType(PrimitiveTypeInfo.STRING);

        var expr = new IndexExpression(arrayVar, indexExpr);
        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }
}
