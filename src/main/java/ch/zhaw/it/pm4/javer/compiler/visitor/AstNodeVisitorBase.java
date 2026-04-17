package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;

import java.util.Optional;

@JacocoGenerated("Dummy class, remove when implemented.")
public abstract class AstNodeVisitorBase<T> implements AstNodeVisitor<T> {

    protected Optional<T> visitDefault(AstNode node) {
        return Optional.empty();
    }

    @Override
    abstract public T visit(CompilationUnit node);

    @Override
    abstract public T visit(EnumDeclaration node);

    @Override
    abstract public T visit(EnumItem node);

    @Override
    abstract public T visit(FunctionDeclaration node);

    @Override
    abstract public T visit(FunctionParameter node);

    @Override
    abstract public T visit(StructDeclaration node);

    @Override
    abstract public T visit(StructField node);

    @Override
    abstract public T visit(BlockStatement node);

    @Override
    abstract public T visit(IfStatement node);

    @Override
    abstract public T visit(WhileStatement node);

    @Override
    abstract public T visit(DoWhileStatement node);

    @Override
    abstract public T visit(ForStatement node);

    @Override
    abstract public T visit(SwitchStatement node);

    @Override
    abstract public T visit(SwitchCase node);

    @Override
    abstract public T visit(BreakStatement node);

    @Override
    abstract public T visit(ContinueStatement node);

    @Override
    abstract public T visit(ReturnStatement node);

    @Override
    abstract public T visit(VarDeclarationStatement node);

    @Override
    abstract public T visit(AssignExpression node);

    @Override
    abstract public T visit(ConditionalExpression node);

    @Override
    abstract public T visit(BinaryExpression node);

    @Override
    abstract public T visit(UnaryExpression node);

    @Override
    abstract public T visit(PostfixExpression node);

    @Override
    abstract public T visit(CallExpression node);

    @Override
    abstract public T visit(IndexExpression node);

    @Override
    abstract public T visit(MemberAccessExpression node);

    @Override
    abstract public T visit(NewExpression node);

    @Override
    abstract public T visit(ArrayInitExpression node);

    @Override
    abstract public T visit(NameExpression node);

    @Override
    abstract public T visit(LiteralExpression<?> node);

    @Override
    abstract public T visit(LiteralCaseLabel node);

    @Override
    abstract public T visit(EnumCaseLabel node);

    @Override
    abstract public T visit(ArrayType node);

    @Override
    abstract public T visit(NamedType node);

    @Override
    abstract public T visit(PrimitiveType node);

    @Override
    abstract public T visit(VoidType node);

    @Override
    abstract public T visit(ForInitVarDeclaration node);

    @Override
    abstract public T visit(ForInitExpressionList node);
}
