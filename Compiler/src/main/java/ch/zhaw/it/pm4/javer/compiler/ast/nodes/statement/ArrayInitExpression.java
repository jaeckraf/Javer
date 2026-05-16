package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

/**
 * Expression node representing an array initializer.
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ArrayInitExpression extends ExpressionAstNodeBase {

    private final List<ExpressionAstNode> elements;

    public ArrayInitExpression(List<ExpressionAstNode> elements) {
        this.elements = elements;
    }

    public List<ExpressionAstNode> getElements() {
        return elements;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
