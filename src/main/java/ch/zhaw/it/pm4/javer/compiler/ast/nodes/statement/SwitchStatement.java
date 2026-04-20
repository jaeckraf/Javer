package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class SwitchStatement implements StatementAstNode {

    private final ExpressionAstNode condition;
    private final List<SwitchCase> cases;

    public SwitchStatement(ExpressionAstNode condition, List<SwitchCase> cases) {
        this.condition = condition;
        this.cases = cases;
    }

    public List<SwitchCase> getCases() {
        return cases;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
