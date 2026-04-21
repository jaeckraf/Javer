package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ArrayInitExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.AssignExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BinaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.CallExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ConditionalExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IndexExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NewExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.PostfixExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.UnaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;

public class SymbolTableCreation extends AstNodeVisitorBase<Void> {

    private SymbolTable symbolTable;

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
        return null;
    }

    @Override
    public Void visit(EnumItem node) {
        return null;
    }

    @Override
    public Void visit(FunctionDeclaration node) {
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
        VariableSymbolTableEntry entry =
        new VariableSymbolTableEntry(
            node.getName(),
            node.getType(),
            null
        );

        symbolTable.addEntry(entry);

    return null;
    }

    @Override
    public Void visit(StructDeclaration node) {
        return null;
    }

    @Override
    public Void visit(StructField node) {
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
    public Void visit(BreakStatement node) {
        return null;
    }

    @Override
    public Void visit(ContinueStatement node) {
        return null;
    }

    @Override
    public Void visit(ReturnStatement node) {
        return null;
    }

    @Override
    public Void visit(VarDeclarationStatement node) {
        VariableSymbolTableEntry entry = new VariableSymbolTableEntry(
            node.getName(),
            node.getType(),
            node.getInitializer()
        );
        symbolTable.addEntry(entry);
        return null;
    }

    @Override
    public Void visit(AssignExpression node) {
        return null;
    }

    @Override
    public Void visit(ConditionalExpression node) {
        return null;
    }

    @Override
    public Void visit(BinaryExpression node) {
        return null;
    }

    @Override
    public Void visit(UnaryExpression node) {
        return null;
    }

    @Override
    public Void visit(PostfixExpression node) {
        return null;
    }

    @Override
    public Void visit(CallExpression node) {
        return null;
    }

    @Override
    public Void visit(IndexExpression node) {
        return null;
    }

    @Override
    public Void visit(MemberAccessExpression node) {
        return null;
    }

    @Override
    public Void visit(NewExpression node) {
        return null;
    }

    @Override
    public Void visit(ArrayInitExpression node) {
        return null;
    }

    @Override
    public Void visit(NameExpression node) {
        return null;
    }

    @Override
    public Void visit(LiteralExpression<?> node) {
        return null;
    }

    @Override
    public Void visit(LiteralCaseLabel node) {
        return null;
    }

    @Override
    public Void visit(EnumCaseLabel node) {
        return null;
    }

    @Override
    public Void visit(ArrayType node) {
        return null;
    }

    @Override
    public Void visit(NamedType node) {
        return null;
    }

    @Override
    public Void visit(PrimitiveType node) {
        return null;
    }

    @Override
    public Void visit(VoidType node) {
        return null;
    }

    @Override
    public Void visit(ForInitVarDeclaration node) {
        return null;
    }

    @Override
    public Void visit(ForInitExpressionList node) {
        return null;
    }
}
