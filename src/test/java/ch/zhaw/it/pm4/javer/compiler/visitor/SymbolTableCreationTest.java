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

class SymbolTableCreationTest {
    private SymbolTableCreation visitor;

    @BeforeEach
    void setUp() {
        visitor = new SymbolTableCreation();
    }

    @Nested
    class FunctionParameterTests {
        @Test
        void visitFunctionParameter_addsEntryToSymbolTable() {
            FunctionParameter param = new FunctionParameter(
                "x",
                new PrimitiveType(PrimitiveTypeKind.INT)
            );
            List<DeclarationAstNode> declarations = new ArrayList<>();
            CompilationUnit compilationUnit = new CompilationUnit(declarations);
            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);
            visitor.visit(param);

            assertTrue(symbolTable.contains("x"));
            assertEquals(PrimitiveTypeKind.INT, ((PrimitiveType) symbolTable.getEntry("x").getType()).getKind());
        }
    }

    @Nested
    class CompilationUnitTests {
        @Test
        void visitCompilationUnit_addsEntryToSymbolTable() {
            List<DeclarationAstNode> declarations = new ArrayList<>();
            
            FunctionDeclaration funcDecl = new FunctionDeclaration(
                new PrimitiveType(PrimitiveTypeKind.BOOL),
                "myFunction",
                List.of(new FunctionParameter("x", new PrimitiveType(PrimitiveTypeKind.INT))),
                null
            );

            declarations.add(funcDecl);
            CompilationUnit compilationUnit = new CompilationUnit(declarations);
            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);

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
            
            List<DeclarationAstNode> declarations = new ArrayList<>();
            CompilationUnit compilationUnit = new CompilationUnit(declarations);
            SymbolTable symbolTable = compilationUnit.getSymbolTable();

            visitor.visit(compilationUnit);
            visitor.visit(varDecl);

            assertTrue(symbolTable.contains("count"));
            VariableSymbolTableEntry entry =
                (VariableSymbolTableEntry) symbolTable.getEntry("count");
            assertEquals(PrimitiveTypeKind.INT, ((PrimitiveType) entry.getType()).getKind());
            assertNull(entry.getInitializer());
        }
    }
}