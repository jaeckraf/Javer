package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.StatementParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public abstract class JumpStmtParseNode extends StatementParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    abstract public <T> T accept(ParseNodeVisitor<T> visitor);

}
