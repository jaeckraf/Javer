package ch.zhaw.it.pm4.javer.compiler.visitor;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.ParameterEntry;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.CallExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.FunctionScope;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeCheckVisitorCallExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    private LiteralExpression<Integer> intLit(int value) {
        return new LiteralExpression<>(LiteralKind.INT, value );
    }

    private LiteralExpression<Double> doubleLit(double value) {
        return new LiteralExpression<>(LiteralKind.DOUBLE, value);
    }

    private LiteralExpression<String> stringLit(String value) {
        return new LiteralExpression<>(LiteralKind.STRING, value);
    }

    // --- Positive: void built-in ---
    @Test
    void testVoidBuiltInFunction() {
        CallExpression expr = new CallExpression("prints", List.of(stringLit("hi")));
        expr.accept(visitor);
        assertEquals(VoidTypeInfo.INSTANCE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Positive: matching parameters ---
    @Test
    void testMatchingParameters() {
        // Funktion: foo(int, double)
        ParameterEntry param1 = mock(ParameterEntry.class);
        when(param1.getType()).thenReturn(PrimitiveTypeInfo.INT);
        ParameterEntry param2 = mock(ParameterEntry.class);
        when(param2.getType()).thenReturn(PrimitiveTypeInfo.DOUBLE);

        FunctionScope scope = mock(FunctionScope.class);
        when(scope.getParameters()).thenReturn(Map.of("a", param1, "b", param2));

        FunctionEntry function = mock(FunctionEntry.class);
        when(function.getScope()).thenReturn(scope);
        when(function.getReturnType()).thenReturn(PrimitiveTypeInfo.DOUBLE);

        CallExpression expr = new CallExpression("foo", List.of(intLit(1), doubleLit(2.0)));
        expr.setResolvedFunction(function);

        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    // --- Negative: non-existing function ---
    @Test
    void testNonExistingFunction() {
        CallExpression expr = new CallExpression("doesNotExist", List.of());
        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: parameter count mismatch (too few) ---
    @Test
    void testParameterCountMismatchTooFew() {
        ParameterEntry param1 = mock(ParameterEntry.class);
        when(param1.getType()).thenReturn(PrimitiveTypeInfo.INT);

        FunctionScope scope = mock(FunctionScope.class);
        when(scope.getParameters()).thenReturn(Map.of("a", param1));

        FunctionEntry function = mock(FunctionEntry.class);
        when(function.getScope()).thenReturn(scope);
        when(function.getReturnType()).thenReturn(PrimitiveTypeInfo.INT);

        CallExpression expr = new CallExpression("foo", List.of());
        expr.setResolvedFunction(function);

        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: parameter count mismatch (too many) ---
    @Test
    void testParameterCountMismatchTooMany() {
        ParameterEntry param1 = mock(ParameterEntry.class);
        when(param1.getType()).thenReturn(PrimitiveTypeInfo.INT);

        FunctionScope scope = mock(FunctionScope.class);
        when(scope.getParameters()).thenReturn(Map.of("a", param1));

        FunctionEntry function = mock(FunctionEntry.class);
        when(function.getScope()).thenReturn(scope);
        when(function.getReturnType()).thenReturn(PrimitiveTypeInfo.INT);

        CallExpression expr = new CallExpression("foo", List.of(intLit(1), intLit(2)));
        expr.setResolvedFunction(function);

        expr.accept(visitor);
        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }

    // --- Negative: type mismatch in parameters ---
    @Test
    void testParameterTypeMismatch() {
        ParameterEntry param1 = mock(ParameterEntry.class);
        when(param1.getType()).thenReturn(PrimitiveTypeInfo.INT);
        ParameterEntry param2 = mock(ParameterEntry.class);
        when(param2.getType()).thenReturn(PrimitiveTypeInfo.DOUBLE);

        FunctionScope scope = mock(FunctionScope.class);
        when(scope.getParameters()).thenReturn(Map.of("a", param1, "b", param2));

        FunctionEntry function = mock(FunctionEntry.class);
        when(function.getScope()).thenReturn(scope);
        when(function.getReturnType()).thenReturn(PrimitiveTypeInfo.DOUBLE);

        // Übergib absichtlich falsche Typen (z.B. String statt Double)
        CallExpression expr = new CallExpression("foo", List.of(intLit(1), stringLit("fail")));
        expr.setResolvedFunction(function);

        expr.accept(visitor);
        assertEquals(PrimitiveTypeInfo.DOUBLE, expr.getResultingType()); // Typ bleibt, aber Fehler wird gemeldet
        assertTrue(bag.hasErrors());
    }
}
