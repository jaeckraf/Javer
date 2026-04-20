package ch.zhaw.it.pm4.javer.compiler.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;

class SymbolTableCreationTest {

    private SymbolTable symbolTable;
    private SymbolTableCreation visitor;

    @BeforeEach
    void setUp() {
        symbolTable = new SymbolTable();
        visitor = new SymbolTableCreation(symbolTable);
    }

    @Nested
    class FunctionParameterTests {
        @Test
        void visitFunctionParameter_addsEntryToSymbolTable() {
            FunctionParameter param = new FunctionParameter(
                "x",
                new PrimitiveType(PrimitiveTypeKind.INT)
            );

            param.accept(visitor);

            assertTrue(symbolTable.contains("x"));
            assertEquals(PrimitiveTypeKind.INT, ((PrimitiveType) symbolTable.getEntry("x").getType()).getKind());
        }
    }

    @Nested
    class VarDeclarationTests {
        @Test
        void visitVarDeclaration_addsVariableEntry() {
            VarDeclarationStatement varDecl = VarDeclarationStatement
                .builder(new PrimitiveType(PrimitiveTypeKind.INT), "count")
                .build();

            varDecl.accept(visitor);

            assertTrue(symbolTable.contains("count"));
            VariableSymbolTableEntry entry =
                (VariableSymbolTableEntry) symbolTable.getEntry("count");
            assertEquals(PrimitiveTypeKind.INT, ((PrimitiveType) entry.getType()).getKind());
            assertNull(entry.getInitializer());
        }
    }
}