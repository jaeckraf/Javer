package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class CaseClauseParseNode extends AbstractCaseClauseParseNode {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
