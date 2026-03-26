package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.StatementParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class IfStmtParseNode extends StatementParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
