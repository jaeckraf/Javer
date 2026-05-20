package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TypeCheckVisitorReturnStatementTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    // --- Helper to create literals with type set ---

    private LiteralExpression<Integer> intLit(int value) {
        LiteralExpression<Integer> lit = new LiteralExpression<>(LiteralKind.INT, value);
        lit.setResultingType(PrimitiveTypeInfo.INT);
        return lit;
    }

    private LiteralExpression<Double> doubleLit(double value) {
        LiteralExpression<Double> lit = new LiteralExpression<>(LiteralKind.DOUBLE, value);
        lit.setResultingType(PrimitiveTypeInfo.DOUBLE);
        return lit;
    }

    // --- Helper to set currentFunctionReturnType via reflection ---

    private void setCurrentFunctionReturnType(TypeCheckVisitor visitor, ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo returnType) {
        try {
            java.lang.reflect.Field field = TypeCheckVisitor.class.getDeclaredField("currentFunctionReturnType");
            field.setAccessible(true);
            field.set(visitor, returnType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Positive: return type matches ---

    @Test
    void testReturnIntValueInIntFunction() {
        // fn int foo() { return 42; }
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.INT);
        ReturnStatement ret = ReturnStatement.builder(intLit(42)).build();

        ret.accept(visitor);

        assertFalse(bag.hasErrors(), "Return int in int function should not produce errors");
    }

    @Test
    void testReturnDoubleValueInDoubleFunction() {
        // fn double bar() { return 3.14; }
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.DOUBLE);
        ReturnStatement ret = ReturnStatement.builder(doubleLit(3.14)).build();

        ret.accept(visitor);

        assertFalse(bag.hasErrors(), "Return double in double function should not produce errors");
    }

    // --- Positive: return without value in void function ---

    @Test
    void testReturnWithoutValueInVoidFunction() {
        // fn void baz() { return; }
        setCurrentFunctionReturnType(visitor, VoidTypeInfo.INSTANCE);
        ReturnStatement ret = ReturnStatement.builder().build();

        ret.accept(visitor);

        assertFalse(bag.hasErrors(), "Return without value in void function should not produce errors");
    }

    // --- Negative: return value in void function ---

    @Test
    void testReturnValueInVoidFunction() {
        // fn void foo() { return 42; }
        setCurrentFunctionReturnType(visitor, VoidTypeInfo.INSTANCE);
        ReturnStatement ret = ReturnStatement.builder(intLit(42)).build();

        ret.accept(visitor);

        assertTrue(bag.hasErrors(), "Return value in void function should produce an error");
    }

    // --- Negative: return wrong type ---

    @Test
    void testReturnDoubleInIntFunction() {
        // fn int foo() { return 3.14; }
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.INT);
        ReturnStatement ret = ReturnStatement.builder(doubleLit(3.14)).build();

        ret.accept(visitor);

        assertTrue(bag.hasErrors(), "Return double in int function should produce an error");
    }

    @Test
    void testReturnIntInDoubleFunction() {
        // fn double foo() { return 42; } ✓ (implicit conversion allowed)
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.DOUBLE);
        ReturnStatement ret = ReturnStatement.builder(intLit(42)).build();

        ret.accept(visitor);

        assertFalse(bag.hasErrors(), "Return int in double function should be allowed (implicit conversion)");
    }

    // --- Negative: return no value in non-void function ---

    @Test
    void testReturnNoValueInIntFunction() {
        // fn int foo() { return; }
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.INT);
        ReturnStatement ret = ReturnStatement.builder().build();

        ret.accept(visitor);

        assertTrue(bag.hasErrors(), "Return without value in non-void function should produce an error");
    }

    @Test
    void testReturnUnknownTypeIgnoresError() {
        // If the expression type is unknown, don't cascade errors
        setCurrentFunctionReturnType(visitor, PrimitiveTypeInfo.INT);

        LiteralExpression<?> unknownExpr = new LiteralExpression<>(LiteralKind.NULL, null);
        unknownExpr.setResultingType(UnknownTypeInfo.INSTANCE);

        ReturnStatement ret = ReturnStatement.builder(unknownExpr).build();

        ret.accept(visitor);

        assertFalse(bag.hasErrors(), "Unknown type in return should not produce additional errors");
    }

    @Test
    void testReturnWithoutContextDefaultsToUnknown() {
        // If currentFunctionReturnType is not set (edge case)
        ReturnStatement ret = ReturnStatement.builder(intLit(42)).build();

        ret.accept(visitor);

        // Should not crash, might produce error or not depending on implementation
        assertFalse(bag.hasErrors(), "Should handle missing context gracefully");
    }
}
