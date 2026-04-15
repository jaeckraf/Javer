package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class NamedType implements TypeAstNode {

    private final NameTypeKind kind;
    private final String name;

    public NamedType(NameTypeKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
