package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;

public class AstPrinter<Void> extends AstNodeVisitorBase<Void> {



    @Override
    public Void visit(CompilationUnit node) {
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
        return null;
    }

    @Override
    public Void visit(FunctionParameter node) {
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
        return null;
    }

    @Override
    public Void visit(IfStatement node) {
        return null;
    }

    @Override
    public Void visit(WhileStatement node) {
        return null;
    }

    @Override
    public Void visit(DoWhileStatement node) {
        return null;
    }

    @Override
    public Void visit(ForStatement node) {
        return null;
    }

    @Override
    public Void visit(SwitchStatement node) {
        return null;
    }

    @Override
    public Void visit(SwitchCase node) {
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
