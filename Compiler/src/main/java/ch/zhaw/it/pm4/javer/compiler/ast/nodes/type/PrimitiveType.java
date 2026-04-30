package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class PrimitiveType extends AstNodeBase implements TypeAstNode {

    private final PrimitiveTypeKind kind;

    public PrimitiveType(PrimitiveTypeKind kind) {
        this.kind = kind;
    }

    public PrimitiveTypeKind getKind() {
        return kind;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
