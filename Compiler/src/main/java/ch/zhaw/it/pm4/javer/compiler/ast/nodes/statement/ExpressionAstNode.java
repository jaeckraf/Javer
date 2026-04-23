package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

public sealed interface ExpressionAstNode extends AstNode, StatementAstNode
        permits AssignExpression, ConditionalExpression, BinaryExpression, UnaryExpression, PostfixExpression,
        CallExpression, IndexExpression, MemberAccessExpression, NewExpression,
        ArrayInitExpression, NameExpression, LiteralExpression {
}
