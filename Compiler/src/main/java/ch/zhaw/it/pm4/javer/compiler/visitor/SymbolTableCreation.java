package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.DoWhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IfStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchCase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.WhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NameTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class SymbolTableCreation extends AstNodeVisitorBase {

    private SymbolTable currentScope;
    private final DiagnosticBag diagnosticBag;

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        currentScope = node.getSymbolTable();

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

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

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

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .parameters(node.getParameters())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

        SymbolTable functionScope = new SymbolTable(currentScope);
        node.setSymbolTable(functionScope);
        currentScope = functionScope;

        for (FunctionParameter param : node.getParameters()) {
            param.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        currentScope = currentScope.getParent(); // exit function scope
    }

    @Override
    public void visit(FunctionParameter node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }

    @Override
    public void visit(StructDeclaration node) {
        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .fields(node.getFields())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

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

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }

    @Override
    public void visit(BlockStatement node) {
        SymbolTable blockScope = new SymbolTable(currentScope);
        node.setSymbolTable(blockScope);
        currentScope = blockScope;
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }

        currentScope = currentScope.getParent(); // exit block scope
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
        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }
}
