package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class IfStatement implements StatementAstNode {

    private final ExpressionAstNode condition;
    private final StatementAstNode thenBranch;
    private final StatementAstNode elseBranch;

    private IfStatement(Builder builder) {
        this.condition = builder.condition;
        this.thenBranch = builder.thenBranch;
        this.elseBranch = builder.elseBranch;
    }

    public static Builder builder(ExpressionAstNode condition, StatementAstNode thenBranch) {
        return new Builder(condition, thenBranch);
    }

    public static final class Builder {
        private final ExpressionAstNode condition;
        private final StatementAstNode thenBranch;
        private StatementAstNode elseBranch;

        private Builder(ExpressionAstNode condition, StatementAstNode thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        public Builder elseBranch(StatementAstNode elseBranch) {
            this.elseBranch = elseBranch;
            return this;
        }

        public IfStatement build() {
            return new IfStatement(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
