package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ConditionalExpression implements ExpressionAstNode {

    private final ExpressionAstNode condition;
    private final ExpressionAstNode trueExpression;
    private final ExpressionAstNode falseExpression;

    private ConditionalExpression(Builder builder) {
        this.condition = builder.condition;
        this.trueExpression = builder.trueExpression;
        this.falseExpression = builder.falseExpression;
    }

    public static Builder builder(ExpressionAstNode condition) {
        return new Builder(condition);
    }

    public static final class Builder {
        private final ExpressionAstNode condition;
        private ExpressionAstNode trueExpression;
        private ExpressionAstNode falseExpression;

        private Builder(ExpressionAstNode condition) {
            this.condition = condition;
        }

        public Builder whenTrue(ExpressionAstNode trueExpression) {
            this.trueExpression = trueExpression;
            return this;
        }

        public Builder whenFalse(ExpressionAstNode falseExpression) {
            this.falseExpression = falseExpression;
            return this;
        }

        public ConditionalExpression build() {
            return new ConditionalExpression(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
