package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.StatementAstNode;

public sealed interface ExpressionAstNode extends AstNode, StatementAstNode
        permits AssignExpression, ConditionalExpression, BinaryExpression, UnaryExpression, PostfixExpression,
        CallExpression, IndexExpression, MemberAccessExpression, NewExpression,
        ArrayInitExpression, NameExpression, LiteralExpression {
}
