package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class PostfixExpression implements ExpressionAstNode {

    private final ExpressionAstNode operand;
    private final PostfixOperationKind kind;

    public PostfixExpression(ExpressionAstNode operand, PostfixOperationKind kind) {
        this.operand = operand;
        this.kind = kind;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
