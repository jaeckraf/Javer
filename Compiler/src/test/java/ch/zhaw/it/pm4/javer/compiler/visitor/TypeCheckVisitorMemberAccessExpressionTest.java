package ch.zhaw.it.pm4.javer.compiler.visitor;
import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumValueEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FieldEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeCheckVisitorMemberAccessExpressionTest {

    private DiagnosticBag bag;
    private TypeCheckVisitor visitor;
    private NameExpression target;

    @BeforeEach
    void setUp() {
        bag = new DiagnosticBag("test.jv", 10, CompilationPhase.TYPE_CHECKING, mock(SourceCache.class));
        visitor = new TypeCheckVisitor(bag);
        target = mock(NameExpression.class);
    }

    @Test
    void testStructFieldAccess() {
        FieldEntry field = mock(FieldEntry.class);
        when(field.getType()).thenReturn(PrimitiveTypeInfo.INT);

        MemberAccessExpression expr = new MemberAccessExpression(target, "field");
        expr.setResolvedField(field);

        expr.accept(visitor);

        assertEquals(PrimitiveTypeInfo.INT, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }

    @Test
    void testEnumValueAccess() {
        EnumValueEntry valueEntry = mock(EnumValueEntry.class);
        when(valueEntry.getOwnerEnum()).thenReturn(mock(ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry.class));

        MemberAccessExpression expr = new MemberAccessExpression(target, "ENUM_VALUE");
        expr.setResolvedEnumValue(valueEntry);

        expr.accept(visitor);

        assertTrue(expr.getResultingType() instanceof EnumTypeInfo);
        assertFalse(bag.hasErrors());
    }

    @Test
    void testNonStructOrEnumAccess() {
        MemberAccessExpression expr = new MemberAccessExpression(target, "notAFieldOrEnum");

        expr.accept(visitor);

        assertEquals(UnknownTypeInfo.INSTANCE, expr.getResultingType());
        assertFalse(bag.hasErrors());
    }
}
