package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SymbolTableAstPrinterTest {

    @Test
    void printsOnlySymbolTables() {
        CompilationUnit compilationUnit = new CompilationUnit(List.of());

        String output = new SymbolTableAstPrinter().printToString(compilationUnit);

        assertTrue(output.startsWith("================"));
        assertTrue(output.contains("table: global"));
        assertFalse(output.contains("CompilationUnit"));
        assertFalse(output.contains("AST\n==="));
        assertFalse(output.contains("declarations"));
    }
}
