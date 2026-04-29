package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class SwitchStatement extends AstNodeBase implements StatementAstNode {

    private final ExpressionAstNode condition;
    private final List<SwitchCase> cases;

    public SwitchStatement(ExpressionAstNode condition, List<SwitchCase> cases) {
        this.condition = condition;
        this.cases = cases;
    }

    public ExpressionAstNode getCondition() {
        return condition;
    }

    public List<SwitchCase> getCases() {
        return cases;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
