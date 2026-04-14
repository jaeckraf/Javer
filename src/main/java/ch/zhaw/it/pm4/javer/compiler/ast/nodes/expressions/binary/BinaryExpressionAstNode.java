package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.binary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class BinaryExpressionAstNode extends ExpressionAstNode {

    private final BinaryExpressionType type;
    private final ExpressionAstNode left;
    private final ExpressionAstNode right;

    public BinaryExpressionAstNode(BinaryExpressionType type, ExpressionAstNode left, ExpressionAstNode right) {
        this.type = type;
        this.left = left;
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
