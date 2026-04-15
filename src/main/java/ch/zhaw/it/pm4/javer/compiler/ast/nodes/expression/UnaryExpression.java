package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class UnaryExpression implements ExpressionAstNode {

    private final UnaryExpressionKind kind;
    private final ExpressionAstNode operand;

    public UnaryExpression(UnaryExpressionKind kind, ExpressionAstNode operand) {
        this.kind = kind;
        this.operand = operand;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
