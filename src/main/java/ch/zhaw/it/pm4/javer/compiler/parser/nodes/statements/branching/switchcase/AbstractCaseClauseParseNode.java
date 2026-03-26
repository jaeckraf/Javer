package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public abstract class AbstractCaseClauseParseNode extends ParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    abstract public <T> T accept(ParseNodeVisitor<T> visitor);

}
