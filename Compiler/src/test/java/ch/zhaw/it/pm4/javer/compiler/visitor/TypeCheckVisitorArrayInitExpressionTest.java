package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ArrayInitExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TypeCheckVisitorArrayInitExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        // SourceCache kann ein Mock sein, falls nötig
        SourceCache dummySourceCache = mock(SourceCache.class);
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, dummySourceCache);
        visitor = new TypeCheckVisitor(bag);
    }

    @Test
    void testEmptyArrayInit() {
        ArrayInitExpression expr = new ArrayInitExpression(List.of());
        expr.accept(visitor);
        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(UnknownTypeInfo.INSTANCE, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testArrayInitWithSameTypeElements() {
        var e1 = new LiteralExpression<>(LiteralKind.INT, 1);
        var e2 = new LiteralExpression<>(LiteralKind.INT, 2);
        e1.setResultingType(PrimitiveTypeInfo.INT);
        e2.setResultingType(PrimitiveTypeInfo.INT);

        ArrayInitExpression expr = new ArrayInitExpression(List.of(e1, e2));
        expr.accept(visitor);
        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(PrimitiveTypeInfo.INT, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testArrayInitWithDifferentTypes() {
        var e1 = new LiteralExpression<>(LiteralKind.INT, 1);
        var e2 = new LiteralExpression<>(LiteralKind.DOUBLE, 2.0);
        e1.setResultingType(PrimitiveTypeInfo.INT);
        e2.setResultingType(PrimitiveTypeInfo.DOUBLE);

        ArrayInitExpression expr = new ArrayInitExpression(List.of(e1, e2));
        expr.accept(visitor);
        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(UnknownTypeInfo.INSTANCE, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testArrayInitWithSingleElement() {
        var e1 = new LiteralExpression<>(LiteralKind.BOOLEAN, true);
        e1.setResultingType(PrimitiveTypeInfo.BOOL);

        ArrayInitExpression expr = new ArrayInitExpression(List.of(e1));
        expr.accept(visitor);
        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(PrimitiveTypeInfo.BOOL, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertFalse(bag.hasErrors());
    }
}
