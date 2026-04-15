package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class ArrayType implements TypeAstNode {

    private final TypeAstNode baseType;

    public ArrayType(TypeAstNode baseType) {
        this.baseType = baseType;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
