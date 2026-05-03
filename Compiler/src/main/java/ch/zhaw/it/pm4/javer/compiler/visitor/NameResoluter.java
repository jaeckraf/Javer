package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.DoWhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IfStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchCase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.WhileStatement;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class NameResoluter extends AstNodeVisitorBase {
    private SymbolTable currentScope;
    private final DiagnosticBag diagnosticBag;

    public NameResoluter(DiagnosticBag diagnosticBag) {
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
        for (EnumItem item : node.getItems()) {
            item.accept(this);
        }
    }

    @Override
    public void visit(EnumItem node) {
        if (!currentScope.contains(node.getName()))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined symbol: " + node.getName());
    }

    @Override
    public void visit(FunctionDeclaration node) {
        currentScope = node.getSymbolTable();

        for (FunctionParameter param : node.getParameters()) {
            param.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        currentScope = currentScope.getParent(); // exit function scope
    }

    @Override
    public void visit(BlockStatement node) {
        currentScope = node.getSymbolTable();
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
    public void visit(NameExpression node) {
        if (!currentScope.contains(node.getName()))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined symbol: " + node.getName());
    }
}
