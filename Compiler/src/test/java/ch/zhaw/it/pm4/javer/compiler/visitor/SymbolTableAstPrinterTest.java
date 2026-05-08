package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.EnumScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.FunctionScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.GlobalScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.StructScope;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SymbolTableAstPrinterTest {

    @Test
    void printsSymbolTableAsTablesBeforeAst() {
        CompilationUnit node = new CompilationUnit(List.of());
        GlobalScope globalScope = node.getGlobalScope();

        EnumEntry enumEntry = new EnumEntry("e");
        enumEntry.setScope(new EnumScope());
        globalScope.defineEnum(enumEntry);

        FunctionEntry functionEntry = new FunctionEntry("f");
        functionEntry.setReturnType(VoidTypeInfo.INSTANCE);
        functionEntry.setScope(new FunctionScope(functionEntry));
        globalScope.defineFunction(functionEntry);

        StructEntry structEntry = new StructEntry("s");
        structEntry.setScope(new StructScope());
        globalScope.defineStruct(structEntry);

        String actual = normalize(new SymbolTableAstPrinter().printToString(node));

        assertTrue(actual.contains("| table: global | scope offset: 0"));
        assertTrue(actual.contains("| enum | e"));
        assertTrue(actual.contains("| table: function f | return: void | label: '_f' | parameter bytes: 0 | local bytes: 0 | frame bytes: 0"));
        assertTrue(actual.contains("| table: struct s | size bytes: 0"));
        assertTrue(actual.contains("AST\n===\n\u2514\u2500\u2500 declarations (0)"));
        assertFalse(actual.contains("globalScope"));
    }

    private static String normalize(String value) {
        return value.replace("\r\n", "\n").strip();
    }
}
