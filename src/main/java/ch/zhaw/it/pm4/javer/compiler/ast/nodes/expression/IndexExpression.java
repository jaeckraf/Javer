package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class IndexExpression implements ExpressionAstNode {

    private final ExpressionAstNode target;
    private final ExpressionAstNode index;

    public IndexExpression(ExpressionAstNode target, ExpressionAstNode index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
