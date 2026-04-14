package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.literal;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class LiteralConstantAstNode<V> extends ExpressionAstNode {

    private final V value;

    public LiteralConstantAstNode(V value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }

}
