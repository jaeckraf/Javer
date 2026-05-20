package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BinaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BinaryExpressionKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TypeCheckVisitorBinaryExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        String dummyFilePath = "test.jv";
        int errorLimit = 10;
        CompilationPhase phase = CompilationPhase.TYPE_CHECKING;
        SourceCache dummySourceCache = mock(SourceCache.class);

        bag = new DiagnosticBag(dummyFilePath, errorLimit, phase, dummySourceCache);
        visitor = new TypeCheckVisitor(bag);
    }


    private TypeCheckVisitor createVisitor(DiagnosticBag bag) {
        return new TypeCheckVisitor(bag);
    }

    private LiteralExpression<Integer> intLit(int value) {
        return new LiteralExpression<>(LiteralKind.INT, value);
    }

    private LiteralExpression<Double> doubleLit(double value) {
        return new LiteralExpression<>(LiteralKind.DOUBLE, value);
    }

    private LiteralExpression<Boolean> boolLit(boolean value) {
        return new LiteralExpression<>(LiteralKind.BOOLEAN, value);
    }

    private LiteralExpression<String> stringLit(String value) {
        return new LiteralExpression<>(LiteralKind.STRING, value);
    }


    // --- Positive Normal Cases ---

    @Test
    void testAdditionOfInts() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.ADD,
                intLit(1),
                intLit(2)
        );

        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testLogicalAndOfBooleans() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.AND,
                boolLit(true),
                boolLit(false)
        );

        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.BOOL, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Positive Edge Cases ---

    @Test
    void testAdditionOfIntAndDouble() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.ADD,
                intLit(1),
                doubleLit(2.5)
        );

        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testComparisonIntLessDouble() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.LESS,
                intLit(1),
                doubleLit(2.5)
        );

        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.BOOL, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Negative Cases ---

    @Test
    void testAdditionOfIntAndBoolShouldFail() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.ADD,
                intLit(1),
                boolLit(true)
        );

        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testEqualsStringAndIntShouldFail() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.EQUALS,
                stringLit("abc"),
                intLit(1)
        );

        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    @Test
    void testBitwiseAndOnDoublesShouldFail() {
        BinaryExpression expr = new BinaryExpression(
                BinaryExpressionKind.BITWISE_AND,
                doubleLit(1.5),
                doubleLit(2.5)
        );

        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }
}
