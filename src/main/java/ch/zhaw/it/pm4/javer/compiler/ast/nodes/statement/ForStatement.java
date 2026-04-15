package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class ForStatement implements StatementAstNode {

    private ForInit forInit;
    private ExpressionAstNode condition;
    private List<ExpressionAstNode> update;
    private StatementAstNode body;

    public ForStatement(ForInit forInit, ExpressionAstNode condition, List<ExpressionAstNode> update, StatementAstNode body) {
        this.forInit = forInit;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
