package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TypeCheckVisitorIfStatementTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;
    private BlockStatement thenBranch;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
        thenBranch = mock(BlockStatement.class);
    }

    @Test
    void testIfWithBooleanCondition() {
        // if (true) {...}
        LiteralExpression<Boolean> condition = new LiteralExpression<>(LiteralKind.BOOLEAN, true);
        condition.setResultingType(PrimitiveTypeInfo.BOOL);

        IfStatement ifStmt = IfStatement.builder(condition, thenBranch).build();
        ifStmt.accept(visitor);

        assertFalse(bag.hasErrors(), "Boolean condition should not produce errors");
    }

    @Test
    void testIfWithIntegerCondition() {
        // if (1) {...}
        LiteralExpression<Integer> condition = new LiteralExpression<>(LiteralKind.INT,1);
        condition.setResultingType(PrimitiveTypeInfo.INT);

        IfStatement ifStmt = IfStatement.builder(condition, thenBranch).build();
        ifStmt.accept(visitor);

        assertFalse(bag.hasErrors(), "Integer condition should not produce errors");
    }

    @Test
    void testIfWithDoubleCondition() {
        // if (1.0) {...}
        LiteralExpression<Double> condition = new LiteralExpression<>(LiteralKind.DOUBLE,1.0);
        condition.setResultingType(PrimitiveTypeInfo.DOUBLE);

        IfStatement ifStmt = IfStatement.builder(condition, thenBranch).build();
        ifStmt.accept(visitor);

        assertFalse(bag.hasErrors(), "Double condition should not produce errors");
    }

    @Test
    void testIfWithStringCondition() {
        // if ("hello") {...}
        LiteralExpression<String> condition = new LiteralExpression<>(LiteralKind.STRING, "hello");
        condition.setResultingType(PrimitiveTypeInfo.STRING);

        IfStatement ifStmt = IfStatement.builder(condition, thenBranch).build();
        ifStmt.accept(visitor);

        assertTrue(bag.hasErrors(), "String condition should produce an error");
    }

    @Test
    void testIfWithUnknownCondition() {
        // if (unknown) {...}
        LiteralExpression<?> condition = new LiteralExpression<>(LiteralKind.NULL, null);
        condition.setResultingType(UnknownTypeInfo.INSTANCE);

        IfStatement ifStmt = IfStatement.builder(condition, thenBranch).build();
        ifStmt.accept(visitor);

        assertFalse(bag.hasErrors(), "Unknown type should not produce additional errors (to avoid error cascade)");
    }
}
