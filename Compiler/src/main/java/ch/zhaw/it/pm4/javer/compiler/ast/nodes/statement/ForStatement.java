package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
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

    public StatementAstNode getBody() {
        return body;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
