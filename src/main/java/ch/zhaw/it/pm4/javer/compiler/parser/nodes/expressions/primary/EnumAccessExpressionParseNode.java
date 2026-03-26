package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class EnumAccessExpressionParseNode extends ExpressionParseNode {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
