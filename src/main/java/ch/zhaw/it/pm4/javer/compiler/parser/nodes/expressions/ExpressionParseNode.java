package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.ExpressionStatementParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public abstract class ExpressionParseNode extends ExpressionStatementParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    abstract public <T> T accept(ParseNodeVisitor<T> visitor);

}
