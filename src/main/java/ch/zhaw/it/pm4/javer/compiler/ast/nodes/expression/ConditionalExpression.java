package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class ConditionalExpression implements ExpressionAstNode {

    private final ExpressionAstNode condition;
    private ExpressionAstNode trueExpression;

    public ConditionalExpression(ExpressionAstNode condition, ExpressionAstNode trueExpression, ExpressionAstNode falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    private ExpressionAstNode falseExpression;

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
