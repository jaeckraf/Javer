package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class AssignExpression implements ExpressionAstNode {

    private final ExpressionAstNode target;
    private AssignOperator operator;
    private ExpressionAstNode value;

    public AssignExpression(ExpressionAstNode target, AssignOperator operator, ExpressionAstNode value) {
        this.target = target;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
