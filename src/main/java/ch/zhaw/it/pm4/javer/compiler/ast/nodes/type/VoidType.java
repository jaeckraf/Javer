package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class VoidType implements TypeAstNode {

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
