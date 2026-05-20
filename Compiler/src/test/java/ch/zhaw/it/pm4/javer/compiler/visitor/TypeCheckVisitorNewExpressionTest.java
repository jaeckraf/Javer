package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NewExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeCheckVisitorNewExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
    }

    @Test
    void testNewStruct() {
        // new MyStruct()
        StructEntry structEntry = mock(StructEntry.class);
        NamedType structType = mock(NamedType.class);
        when(structType.getResolvedEntry()).thenReturn(structEntry);

        NewExpression expr = NewExpression.builder(structType).build();
        expr.accept(visitor);

        assertTrue(expr.getResultingType() instanceof StructTypeInfo);
        assertFalse(bag.hasErrors());
    }

    @Test
    void testNewArrayWithDimension() {
        // new int[5]
        PrimitiveType intType = new PrimitiveType(PrimitiveTypeKind.INT);
        ArrayType arrayType = new ArrayType(intType);

        var dimExpr = new LiteralExpression<>(LiteralKind.INT, 5);

        NewExpression expr = NewExpression.builder(arrayType)
                .dimensions(List.of(dimExpr))
                .build();
        expr.accept(visitor);

        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(PrimitiveTypeInfo.INT, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testNewArrayWithInit() {
        // new int[] { ... }
        PrimitiveType intType = new PrimitiveType(PrimitiveTypeKind.INT);
        ArrayType arrayType = new ArrayType(intType);
        var arrayInit = mock(ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ArrayInitExpression.class);

        NewExpression expr = NewExpression.builder(arrayType)
                .arrayInit(arrayInit)
                .build();
        expr.accept(visitor);

        assertTrue(expr.getResultingType() instanceof ArrayTypeInfo);
        assertEquals(PrimitiveTypeInfo.INT, ((ArrayTypeInfo) expr.getResultingType()).elementType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testNewWithNonStructType() {
        // new int()
        PrimitiveType intType = new PrimitiveType(PrimitiveTypeKind.INT);
        NewExpression expr = NewExpression.builder(intType).build();
        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertTrue(bag.hasErrors());
    }
}