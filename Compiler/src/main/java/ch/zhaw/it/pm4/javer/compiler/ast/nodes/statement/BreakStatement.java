package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * Statement node representing a {@code break} statement.
 */
public final class BreakStatement extends AstNodeBase implements StatementAstNode {


    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
