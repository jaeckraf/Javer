package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;
import java.util.Objects;

/**
 * Represents a literal expression in the Abstract Syntax Tree (AST).
 *
 * @param <V> the type of the value held by this literal expression
 */
public final class LiteralExpression<V> implements ExpressionAstNode {

    private final LiteralKind kind;
    private final V value;

    /**
     * Constructs a new LiteralExpression with the specified kind and value.
     *
     * @param kind  the literal kind
     * @param value the literal's value
     */
    public LiteralExpression(LiteralKind kind, V value) {
        this.kind = kind;
        this.value = value;
    }

    /**
     * Retrieves the kind of this literal expression.
     *
     * @return the literal kind
     */
    public LiteralKind getKind() {
        return kind;
    }

    /**
     * Retrieves the value of this literal expression.
     *
     * @return the literal's value
     */
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
