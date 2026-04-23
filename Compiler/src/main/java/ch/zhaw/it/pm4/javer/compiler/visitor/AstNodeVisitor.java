package ch.zhaw.it.pm4.javer.compiler.visitor;


import ch.zhaw.it.pm4.javer.compiler.ast.nodes.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.*;
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

public interface AstNodeVisitor<T> {

    T visit(CompilationUnit node);
    T visit(EnumDeclaration node);
    T visit(EnumItem node);
    T visit(FunctionDeclaration node);
    T visit(FunctionParameter node);
    T visit(StructDeclaration node);
    T visit(StructField node);

    T visit(BlockStatement node);
    T visit(IfStatement node);
    T visit(WhileStatement node);
    T visit(DoWhileStatement node);
    T visit(ForStatement node);
    T visit(SwitchStatement node);
    T visit(SwitchCase node);
    T visit(BreakStatement node);
    T visit(ContinueStatement node);
    T visit(ReturnStatement node);
    T visit(VarDeclarationStatement node);

    T visit(AssignExpression node);
    T visit(ConditionalExpression node);
    T visit(BinaryExpression node);
    T visit(UnaryExpression node);
    T visit(PostfixExpression node);
    T visit(CallExpression node);
    T visit(IndexExpression node);
    T visit(MemberAccessExpression node);
    T visit(NewExpression node);
    T visit(ArrayInitExpression node);
    T visit(NameExpression node);
    T visit(LiteralExpression<?> node);

    T visit(LiteralCaseLabel node);
    T visit(EnumCaseLabel node);

    T visit(ArrayType node);
    T visit(NamedType node);
    T visit(PrimitiveType node);
    T visit(VoidType node);

    T visit(ForInitVarDeclaration node);
    T visit(ForInitExpressionList node);

}
