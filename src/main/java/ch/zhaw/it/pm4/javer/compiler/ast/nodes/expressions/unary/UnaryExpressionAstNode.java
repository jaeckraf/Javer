package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.unary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class UnaryExpressionAstNode extends ExpressionAstNode {

    private final UnaryExpressionType type;
    private final ExpressionAstNode operand;

    public UnaryExpressionAstNode(UnaryExpressionType type, ExpressionAstNode operand) {
        this.type = type;
        this.operand = operand;
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
