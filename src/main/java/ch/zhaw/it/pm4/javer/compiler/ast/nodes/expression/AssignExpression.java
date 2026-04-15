package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class AssignExpression implements ExpressionAstNode {

    private final ExpressionAstNode target;
    private final AssignOperator operator;
    private final ExpressionAstNode value;

    private AssignExpression(Builder builder) {
        this.target = builder.target;
        this.operator = builder.operator;
        this.value = builder.value;
    }

    public static Builder builder(ExpressionAstNode target) {
        return new Builder(target);
    }

    public static final class Builder {
        private final ExpressionAstNode target;
        private AssignOperator operator;
        private ExpressionAstNode value;

        private Builder(ExpressionAstNode target) {
            this.target = target;
        }

        public Builder operator(AssignOperator operator) {
            this.operator = operator;
            return this;
        }

        public Builder value(ExpressionAstNode value) {
            this.value = value;
            return this;
        }

        public AssignExpression build() {
            return new AssignExpression(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
