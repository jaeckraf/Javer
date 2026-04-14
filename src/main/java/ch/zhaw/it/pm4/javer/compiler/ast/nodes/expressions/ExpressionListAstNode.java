package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class ExpressionListAstNode extends ExpressionAstNode {

    private final List<ExpressionAstNode> expressions;

    public ExpressionListAstNode(List<ExpressionAstNode> expressions) {
        this.expressions = expressions;
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
