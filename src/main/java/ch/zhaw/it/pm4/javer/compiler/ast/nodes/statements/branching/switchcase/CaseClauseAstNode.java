package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.label.CaseLabelAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class CaseClauseAstNode extends AbstractCaseClauseAstNode {

    private final List<CaseLabelAstNode> caseLabels;
    private final StatementAstNode statement;

    public CaseClauseAstNode(List<CaseLabelAstNode> caseLabels, StatementAstNode statement) {
        this.caseLabels = caseLabels;
        this.statement = statement;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
