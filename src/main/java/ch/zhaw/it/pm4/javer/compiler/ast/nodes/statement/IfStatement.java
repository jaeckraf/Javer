package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class IfStatement implements StatementAstNode {

    private final ExpressionAstNode condition;
    private final StatementAstNode thenBranch;
    private StatementAstNode elseBranch;

    public IfStatement(ExpressionAstNode condition, StatementAstNode thenBranch, StatementAstNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
