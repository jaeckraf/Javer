package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;
import java.util.Objects;

public final class LiteralExpression<V> implements ExpressionAstNode {

    private final LiteralKind kind;
    private final V value;

    public LiteralExpression(LiteralKind kind, V value) {
        this.kind = kind;
        this.value = value;
    }

    public LiteralKind getKind() {
        return kind;
    }

    public V getValue() {
        return value;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralExpression<?> that = (LiteralExpression<?>) o;
        return kind == that.kind && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, value);
    }
}
