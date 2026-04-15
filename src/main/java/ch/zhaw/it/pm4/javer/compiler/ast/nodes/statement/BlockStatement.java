package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class BlockStatement implements StatementAstNode {

    private final List<StatementAstNode> statements;

    public BlockStatement(List<StatementAstNode> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
