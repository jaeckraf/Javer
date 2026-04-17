package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public interface AstNode {

    <T> T accept(AstNodeVisitor<T> visitor);

}
