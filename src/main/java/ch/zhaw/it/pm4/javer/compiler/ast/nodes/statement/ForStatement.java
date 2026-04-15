package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class ForStatement implements StatementAstNode {

    private final ForInit forInit;
    private final ExpressionAstNode condition;
    private final List<ExpressionAstNode> update;
    private final StatementAstNode body;

    private ForStatement(Builder builder) {
        this.forInit = builder.forInit;
        this.condition = builder.condition;
        this.update = builder.update;
        this.body = builder.body;
    }

    public static Builder builder(StatementAstNode body) {
        return new Builder(body);
    }

    public static final class Builder {
        private ForInit forInit;
        private ExpressionAstNode condition;
        private List<ExpressionAstNode> update;
        private final StatementAstNode body;

        private Builder(StatementAstNode body) {
            this.body = body;
        }

        public Builder forInit(ForInit forInit) {
            this.forInit = forInit;
            return this;
        }

        public Builder condition(ExpressionAstNode condition) {
            this.condition = condition;
            return this;
        }

        public Builder update(List<ExpressionAstNode> update) {
            this.update = update;
            return this;
        }

        public ForStatement build() {
            return new ForStatement(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
