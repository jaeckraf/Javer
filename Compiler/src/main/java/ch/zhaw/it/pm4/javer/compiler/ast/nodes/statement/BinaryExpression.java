package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class BinaryExpression extends AstNodeBase implements ExpressionAstNode {

    private final BinaryExpressionKind operator;
    private final ExpressionAstNode left;
    private final ExpressionAstNode right;

    public BinaryExpression(BinaryExpressionKind operator, ExpressionAstNode left, ExpressionAstNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public BinaryExpressionKind getOperator() {
        return operator;
    }

    public ExpressionAstNode getLeft() {
        return left;
    }

    public ExpressionAstNode getRight() {
        return right;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
