package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ReturnStatement implements StatementAstNode {

    private final ExpressionAstNode expression;

    private ReturnStatement(Builder builder) {
        this.expression = builder.expression;
    }

    public ExpressionAstNode getExpression() {
        return expression;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExpressionAstNode expression) {
        return new Builder().expression(expression);
    }

    public static final class Builder {
        private ExpressionAstNode expression;

        public Builder expression(ExpressionAstNode expression) {
            this.expression = expression;
            return this;
        }

        public ReturnStatement build() {
            return new ReturnStatement(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
