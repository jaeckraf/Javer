package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class ExpressionListParseNode extends ExpressionParseNode {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
