package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class UnaryExpression extends AstNodeBase implements ExpressionAstNode {

    private final UnaryExpressionKind kind;
    private final ExpressionAstNode operand;

    public UnaryExpression(UnaryExpressionKind kind, ExpressionAstNode operand) {
        this.kind = kind;
        this.operand = operand;
    }

    public UnaryExpressionKind getKind() {
        return kind;
    }

    public ExpressionAstNode getOperand() {
        return operand;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
