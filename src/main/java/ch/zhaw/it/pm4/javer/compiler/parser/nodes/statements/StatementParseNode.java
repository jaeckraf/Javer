package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public abstract class StatementParseNode extends ParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    abstract public <T> T accept(ParseNodeVisitor<T> visitor);

}
