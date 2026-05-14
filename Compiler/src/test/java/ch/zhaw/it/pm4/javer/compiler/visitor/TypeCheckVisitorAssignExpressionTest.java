package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeCheckVisitorAssignExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    private LiteralExpression<Integer> intLit(int value) {
        return new LiteralExpression<>(LiteralKind.INT, value);
    }

    private LiteralExpression<Double> doubleLit(double value) {
        return new LiteralExpression<>(LiteralKind.DOUBLE, value);
    }

    private NameExpression nameExprWithType(TypeInfo type) {
        NameExpression name = mock(NameExpression.class);
        when(name.getResultingType()).thenReturn(type);
        return name;
    }

    // --- Positive: normal assign (type matches) ---
    @Test
    void testNormalAssignTypeMatches() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.INT);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.ASSIGN)
                .value(intLit(5))
                .build();
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Positive: add-assign with numeric types ---
    @Test
    void testAddAssignWithNumericTypes() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.DOUBLE);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.ADD_ASSIGN)
                .value(doubleLit(2.5))
                .build();
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Positive: bitwise assign with integer types ---
    @Test
    void testBitwiseAssignWithIntegerTypes() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.INT);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.BITWISE_AND_ASSIGN)
                .value(intLit(3))
                .build();
        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Negative: assign with type mismatch ---
    @Test
    void testAssignTypeMismatch() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.INT);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.ASSIGN)
                .value(doubleLit(2.5))
                .build();
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: add-assign with non-numeric types ---
    @Test
    void testAddAssignWithNonNumericTypes() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.BOOL);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.ADD_ASSIGN)
                .value(intLit(1))
                .build();
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: bitwise assign with non-integer types ---
    @Test
    void testBitwiseAssignWithNonIntegerTypes() {
        NameExpression target = nameExprWithType(PrimitiveTypeInfo.DOUBLE);
        AssignExpression expr = AssignExpression.builder(target)
                .operator(AssignOperator.BITWISE_OR_ASSIGN)
                .value(doubleLit(1.5))
                .build();
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: non-assignable target ---
    @Test
    void testNonAssignableTarget() {
        // Erstelle ein Mock-ExpressionAstNode, das kein NameExpression, MemberAccessExpression oder IndexExpression ist
        var nonAssignable = mock(LiteralExpression.class);
        when(nonAssignable.getResultingType()).thenReturn(PrimitiveTypeInfo.INT);

        AssignExpression expr = AssignExpression.builder(nonAssignable)
                .operator(AssignOperator.ASSIGN)
                .value(intLit(1))
                .build();
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }
}
