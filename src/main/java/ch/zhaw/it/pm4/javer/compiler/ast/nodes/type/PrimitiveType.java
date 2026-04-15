package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class PrimitiveType implements TypeAstNode {

    private final PrimitiveTypeKind kind;

    public PrimitiveType(PrimitiveTypeKind kind) {
        this.kind = kind;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
