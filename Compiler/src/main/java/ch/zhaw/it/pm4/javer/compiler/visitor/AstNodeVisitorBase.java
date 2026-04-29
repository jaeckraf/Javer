package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;

public abstract class AstNodeVisitorBase implements AstNodeVisitor {

    protected void visitDefault(AstNode node) {
    }

    @Override
    public void visit(CompilationUnit node) {
        visitDefault(node);
    }

    @Override
    public void visit(EnumDeclaration node) {
        visitDefault(node);
    }

    @Override
    public void visit(EnumItem node) {
        visitDefault(node);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        visitDefault(node);
    }

    @Override
    public void visit(FunctionParameter node) {
        visitDefault(node);
    }

    @Override
    public void visit(StructDeclaration node) {
        visitDefault(node);
    }

    @Override
    public void visit(StructField node) {
        visitDefault(node);
    }

    @Override
    public void visit(BlockStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(IfStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(WhileStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(DoWhileStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(ForStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(SwitchStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(SwitchCase node) {
        visitDefault(node);
    }

    @Override
    public void visit(BreakStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(ContinueStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(ReturnStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        visitDefault(node);
    }

    @Override
    public void visit(AssignExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(ConditionalExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(BinaryExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(UnaryExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(PostfixExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(CallExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(IndexExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(NewExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(ArrayInitExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(NameExpression node) {
        visitDefault(node);
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        visitDefault(node);
    }

    @Override
    public void visit(LiteralCaseLabel node) {
        visitDefault(node);
    }

    @Override
    public void visit(EnumCaseLabel node) {
        visitDefault(node);
    }

    @Override
    public void visit(ArrayType node) {
        visitDefault(node);
    }

    @Override
    public void visit(NamedType node) {
        visitDefault(node);
    }

    @Override
    public void visit(PrimitiveType node) {
        visitDefault(node);
    }

    @Override
    public void visit(VoidType node) {
        visitDefault(node);
    }

    @Override
    public void visit(ForInitVarDeclaration node) {
        visitDefault(node);
    }

    @Override
    public void visit(ForInitExpressionList node) {
        visitDefault(node);
    }
}
