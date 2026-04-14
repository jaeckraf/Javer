package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class SwitchStmtAstNode extends StatementAstNode {

    private final ExpressionAstNode switchExpr;
    private final List<AbstractCaseClauseAstNode> cases;

    public SwitchStmtAstNode(ExpressionAstNode switchExpr, List<AbstractCaseClauseAstNode> cases) {
        this.switchExpr = switchExpr;
        this.cases = cases;
    }

    /**
     * {@inheritDoc}
     * */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
