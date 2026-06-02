package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * AST node representing an array type with an element type.
 */
public final class ArrayType extends AstNodeBase implements TypeAstNode {

    private final TypeAstNode baseType;

    public ArrayType(TypeAstNode baseType) {
        this.baseType = baseType;
    }

    public TypeAstNode getBaseType() {
        return baseType;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
