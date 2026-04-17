package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class LiteralExpression<V> implements ExpressionAstNode {

    private final LiteralKind kind;
    private final V value;

    public LiteralExpression(LiteralKind kind, V value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
