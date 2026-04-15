package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class PostfixExpression implements ExpressionAstNode {

    private final ExpressionAstNode operand;
    private final PostfixOperationKind kind;

    public PostfixExpression(ExpressionAstNode operand, PostfixOperationKind kind) {
        this.operand = operand;
        this.kind = kind;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
