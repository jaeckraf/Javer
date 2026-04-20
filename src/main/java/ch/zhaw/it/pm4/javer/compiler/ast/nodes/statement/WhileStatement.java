package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class WhileStatement implements StatementAstNode {

    private final ExpressionAstNode condition;
    private final StatementAstNode statement;

    public WhileStatement(ExpressionAstNode condition, StatementAstNode statement) {
        this.condition = condition;
        this.statement = statement;
    }

    public StatementAstNode getBody() {
        return statement;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
