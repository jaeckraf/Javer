package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class DefaultClauseAstNode extends AbstractCaseClauseAstNode {

    private final StatementAstNode statement;

    public DefaultClauseAstNode(StatementAstNode statement) {
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
