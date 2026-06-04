package ch.zhaw.it.pm4.javer.compiler.ast.scope;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.VariableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockScopeTest {

    private static FunctionScope functionScope() {
        return new FunctionScope();
    }

    private static VariableEntry variable(int declarationOrder) {
        return new VariableEntry(
                "value",
                PrimitiveTypeInfo.INT,
                PrimitiveTypeInfo.INT.sizeBytes(),
                0,
                false,
                0,
                declarationOrder);
    }

    @Test
    void resolvesOnlyVariablesDeclaredBeforeCurrentDeclarationOrder() {
        FunctionScope functionScope = functionScope();
        BlockScope blockScope = new BlockScope(null, functionScope);
        VariableEntry variable = variable(0);

        assertTrue(blockScope.defineVariable(variable));

        assertNull(blockScope.resolveVisibleVariable("value", 0));
        assertSame(variable, blockScope.resolveVisibleVariable("value", 1));
    }

    @Test
    void searchesParentScopesWithoutIgnoringDeclarationOrder() {
        FunctionScope functionScope = functionScope();
        BlockScope parentScope = new BlockScope(null, functionScope);
        BlockScope childScope = new BlockScope(parentScope, functionScope);
        VariableEntry parentVariable = variable(1);

        assertTrue(parentScope.defineVariable(parentVariable));

        assertNull(childScope.resolveVisibleVariable("value", 1));
        assertSame(parentVariable, childScope.resolveVisibleVariable("value", 2));
    }

    @Test
    void doesNotResolveVariablesFromChildScopesInParentScope() {
        FunctionScope functionScope = functionScope();
        BlockScope parentScope = new BlockScope(null, functionScope);
        BlockScope childScope = new BlockScope(parentScope, functionScope);
        VariableEntry childVariable = variable(0);

        assertTrue(childScope.defineVariable(childVariable));

        assertNull(parentScope.resolveVisibleVariable("value", 1));
    }
}
