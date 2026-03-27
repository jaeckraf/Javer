package ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class ParenthesizedExpressionParseNode extends ExpressionParseNode {

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
