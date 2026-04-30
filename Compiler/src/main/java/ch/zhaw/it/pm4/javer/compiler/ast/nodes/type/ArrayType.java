package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
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
