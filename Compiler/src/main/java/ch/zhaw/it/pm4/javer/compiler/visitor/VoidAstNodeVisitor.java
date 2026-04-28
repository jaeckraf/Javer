package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;

public abstract class VoidAstNodeVisitor implements AstNodeVisitor<Void> {

    protected Void visitDefault(AstNode node) {
        return null;
    }

    @Override
    public Void visit(CompilationUnit node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(EnumDeclaration node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(EnumItem node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(FunctionDeclaration node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(FunctionParameter node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(StructDeclaration node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(StructField node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(BlockStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(IfStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(WhileStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(DoWhileStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ForStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(SwitchStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(SwitchCase node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(BreakStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ContinueStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ReturnStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(VarDeclarationStatement node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(AssignExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ConditionalExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(BinaryExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(UnaryExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(PostfixExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(CallExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(IndexExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(MemberAccessExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(NewExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ArrayInitExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(NameExpression node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(LiteralExpression<?> node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(LiteralCaseLabel node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(EnumCaseLabel node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ArrayType node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(NamedType node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(PrimitiveType node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(VoidType node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ForInitVarDeclaration node) {
        return visitDefault(node);
    }

    @Override
    public Void visit(ForInitExpressionList node) {
        return visitDefault(node);
    }
}
