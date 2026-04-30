package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

public class SymbolTableCreation extends AstNodeVisitorBase {

    private SymbolTable symbolTable;
    private final DiagnosticBag diagnosticBag;

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        symbolTable = node.getSymbolTable();

        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
    }

    @Override
    public void visit(EnumDeclaration node) {
        EnumSymbolTableEntry entry = EnumSymbolTableEntry.builder()
            .name(node.getName())
            .items(node.getItems())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (EnumItem item : node.getItems()) {
            item.accept(this);
        }
    }

    @Override
    public void visit(EnumItem node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(new NamedType(NameTypeKind.ENUM, node.getName())) // or enum type if you model it
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .parameters(node.getParameters())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (FunctionParameter param : node.getParameters()) {
            param.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
    }

    @Override
    public void visit(FunctionParameter node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
    }

    @Override
    public void visit(StructDeclaration node) {
        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .fields(node.getFields())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (StructField field : node.getFields()) {
            field.accept(this);
        }
    }

    @Override
    public void visit(StructField node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
    }

    @Override
    public void visit(BlockStatement node) {
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(IfStatement node) {
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        node.getBody().accept(this);
    }

    @Override
    public void visit(DoWhileStatement node) {
        node.getBody().accept(this);
    }

    @Override
    public void visit(ForStatement node) {
        node.getBody().accept(this);
    }

    @Override
    public void visit(SwitchStatement node) {
        for (SwitchCase switchCase : node.getCases()) {
            switchCase.accept(this);
        }
    }

    @Override
    public void visit(SwitchCase node) {
        if (node.getStatement() != null) {
            node.getStatement().accept(this);
        }
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .initializer(node.getInitializer()) // or omit entirely if optional
            .build();
        symbolTable.addEntry(entry, diagnosticBag);
    }
}
