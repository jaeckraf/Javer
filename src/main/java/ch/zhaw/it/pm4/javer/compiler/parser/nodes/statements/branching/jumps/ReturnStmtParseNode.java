package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class ReturnStmtParseNode extends JumpStmtParseNode {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
