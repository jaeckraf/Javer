package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

class SymbolTableCreationTest {

    private SymbolTableCreation visitor;
    private DiagnosticBag diagnosticBag;

    @BeforeEach
    void setUp() {
        diagnosticBag = new DiagnosticBag("", 100, null, null);
        visitor = new SymbolTableCreation(diagnosticBag);
    }

    // ------------------------------------------------------------
    // FUNCTION PARAMETER TESTS
    // ------------------------------------------------------------
    @Nested
    class FunctionParameterTests {

        @Test
        void visitFunctionParameter_addsEntryToSymbolTable() {
            FunctionParameter param = new FunctionParameter(
                "x",
                new PrimitiveType(PrimitiveTypeKind.INT)
            );

            CompilationUnit compilationUnit =
                new CompilationUnit(new ArrayList<>());

            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);
            visitor.visit(param);

            assertTrue(symbolTable.contains("x"));
            assertEquals(
                PrimitiveTypeKind.INT,
                ((PrimitiveType) symbolTable.getEntry("x").getType()).getKind()
            );
        }

        @Test
        void visitFunctionParameter_duplicate_reportsError() {
            FunctionParameter p1 = new FunctionParameter(
                "x",
                new PrimitiveType(PrimitiveTypeKind.INT)
            );

            FunctionParameter p2 = new FunctionParameter(
                "x",
                new PrimitiveType(PrimitiveTypeKind.INT)
            );

            CompilationUnit compilationUnit =
                new CompilationUnit(new ArrayList<>());

            visitor.visit(compilationUnit);

            visitor.visit(p1);
            visitor.visit(p2); // duplicate

            assertTrue(diagnosticBag.hasErrors());
        }
    }

    // ------------------------------------------------------------
    // COMPILATION UNIT TESTS
    // ------------------------------------------------------------
    @Nested
    class CompilationUnitTests {

        @Test
        void visitCompilationUnit_addsFunctionParameter() {
            FunctionDeclaration funcDecl = new FunctionDeclaration(
                new PrimitiveType(PrimitiveTypeKind.BOOL),
                "myFunction",
                List.of(
                    new FunctionParameter(
                        "x",
                        new PrimitiveType(PrimitiveTypeKind.INT)
                    )
                ),
                null
            );

            List<DeclarationAstNode> declarations = new ArrayList<>();
            declarations.add(funcDecl);

            CompilationUnit compilationUnit =
                new CompilationUnit(declarations);

            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);

            assertTrue(symbolTable.contains("x"));
        }
    }

    // ------------------------------------------------------------
    // VARIABLE DECLARATION TESTS
    // ------------------------------------------------------------
    @Nested
    class VarDeclarationTests {

        @Test
        void visitVarDeclaration_addsVariableEntry() {
            VarDeclarationStatement varDecl =
                VarDeclarationStatement.builder(
                    new PrimitiveType(PrimitiveTypeKind.INT),
                    "count"
                ).build();

            CompilationUnit compilationUnit =
                new CompilationUnit(new ArrayList<>());

            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);
            visitor.visit(varDecl);

            assertTrue(symbolTable.contains("count"));

            VariableSymbolTableEntry entry =
                (VariableSymbolTableEntry) symbolTable.getEntry("count");

            assertEquals(
                PrimitiveTypeKind.INT,
                ((PrimitiveType) entry.getType()).getKind()
            );

            assertNull(entry.getInitializer());
        }

        @Test
        void visitVarDeclaration_duplicateVariable_reportsError() {
            VarDeclarationStatement v1 =
                VarDeclarationStatement.builder(
                    new PrimitiveType(PrimitiveTypeKind.INT),
                    "x"
                ).build();

            VarDeclarationStatement v2 =
                VarDeclarationStatement.builder(
                    new PrimitiveType(PrimitiveTypeKind.INT),
                    "x"
                ).build();

            CompilationUnit compilationUnit =
                new CompilationUnit(new ArrayList<>());

            visitor.visit(compilationUnit);

            visitor.visit(v1);
            visitor.visit(v2);

            assertTrue(diagnosticBag.hasErrors());
        }

        @Test
        void visitVarDeclaration_differentNames_noError() {
            VarDeclarationStatement v1 =
                VarDeclarationStatement.builder(
                    new PrimitiveType(PrimitiveTypeKind.INT),
                    "a"
                ).build();

            VarDeclarationStatement v2 =
                VarDeclarationStatement.builder(
                    new PrimitiveType(PrimitiveTypeKind.INT),
                    "b"
                ).build();

            CompilationUnit compilationUnit =
                new CompilationUnit(new ArrayList<>());

            visitor.visit(compilationUnit);

            visitor.visit(v1);
            visitor.visit(v2);

            assertTrue(compilationUnit.getSymbolTable().contains("a"));
            assertTrue(compilationUnit.getSymbolTable().contains("b"));
        }
    }
}