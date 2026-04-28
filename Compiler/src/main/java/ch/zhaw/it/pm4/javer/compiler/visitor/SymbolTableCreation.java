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

public class SymbolTableCreation extends VoidAstNodeVisitor {

    private SymbolTable symbolTable;
    private final DiagnosticBag diagnosticBag;

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public Void visit(CompilationUnit node) {
        symbolTable = node.getSymbolTable();

        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(EnumDeclaration node) {
        EnumSymbolTableEntry entry = EnumSymbolTableEntry.builder()
            .name(node.getName())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (EnumItem item : node.getItems()) {
            item.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(EnumItem node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(new NamedType(NameTypeKind.ENUM, node.getName())) // or enum type if you model it
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
        return null;
    }

    @Override
    public Void visit(FunctionDeclaration node) {
        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (FunctionParameter param : node.getParameters()) {
            param.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(FunctionParameter node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        return null;
    }

    @Override
    public Void visit(StructDeclaration node) {
        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (StructField field : node.getFields()) {
            field.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(StructField node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
        return null;
    }

    @Override
    public Void visit(BlockStatement node) {
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfStatement node) {
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(WhileStatement node) {
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(DoWhileStatement node) {
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(ForStatement node) {
        node.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(SwitchStatement node) {
        for (SwitchCase switchCase : node.getCases()) {
            switchCase.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(SwitchCase node) {
        if (node.getStatement() != null) {
            node.getStatement().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(VarDeclarationStatement node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .initializer(node.getInitializer()) // or omit entirely if optional
            .build();
        symbolTable.addEntry(entry, diagnosticBag);
        return null;
    }
}
