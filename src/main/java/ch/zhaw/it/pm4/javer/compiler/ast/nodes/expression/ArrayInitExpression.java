package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class ArrayInitExpression implements ExpressionAstNode {

    private final List<ExpressionAstNode> elements;

    public ArrayInitExpression(List<ExpressionAstNode> elements) {
        this.elements = elements;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
