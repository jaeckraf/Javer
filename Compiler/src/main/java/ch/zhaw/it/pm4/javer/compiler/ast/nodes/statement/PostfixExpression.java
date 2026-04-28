package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

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

    public ExpressionAstNode getOperand() {
        return operand;
    }

    public PostfixOperationKind getKind() {
        return kind;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
