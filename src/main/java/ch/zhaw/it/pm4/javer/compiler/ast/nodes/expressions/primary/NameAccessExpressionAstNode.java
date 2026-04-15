package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class NameAccessExpressionAstNode extends ExpressionAstNode {

    private final String name;

    public NameAccessExpressionAstNode(String name) {
        this.name = name;
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
