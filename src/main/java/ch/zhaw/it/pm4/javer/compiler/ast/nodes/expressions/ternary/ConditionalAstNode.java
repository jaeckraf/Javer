package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ternary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class ConditionalAstNode extends ExpressionAstNode {

    private final ExpressionAstNode left;
    private final ExpressionAstNode middle;
    private final ExpressionAstNode right;

    public ConditionalAstNode(ExpressionAstNode left, ExpressionAstNode middle, ExpressionAstNode right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
