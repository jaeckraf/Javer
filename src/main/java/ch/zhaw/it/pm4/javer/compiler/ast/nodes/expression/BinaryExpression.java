package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class BinaryExpression implements ExpressionAstNode {

    private final BinaryExpressionKind operator;
    private final ExpressionAstNode left;
    private final ExpressionAstNode right;

    public BinaryExpression(BinaryExpressionKind operator, ExpressionAstNode left, ExpressionAstNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
