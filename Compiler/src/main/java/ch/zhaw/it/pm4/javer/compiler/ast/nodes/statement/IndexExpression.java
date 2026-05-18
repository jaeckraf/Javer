package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * Expression node representing indexed access into an array-like value.
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class IndexExpression extends ExpressionAstNodeBase {

    private final ExpressionAstNode target;
    private final ExpressionAstNode index;

    public IndexExpression(ExpressionAstNode target, ExpressionAstNode index) {
        this.target = target;
        this.index = index;
    }

    public ExpressionAstNode getTarget() {
        return target;
    }

    public ExpressionAstNode getIndex() {
        return index;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
