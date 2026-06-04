package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * AST node representing the {@code void} type.
 */
public final class VoidType extends AstNodeBase implements TypeAstNode {

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
