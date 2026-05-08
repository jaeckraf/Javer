package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.BlockScope;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class BlockStatement extends AstNodeBase implements StatementAstNode {

    private final List<StatementAstNode> statements;
    private BlockScope blockScope;

    public BlockStatement(List<StatementAstNode> statements) {
        this.statements = statements;
    }

    public List<StatementAstNode> getStatements() {
        return statements;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public BlockScope getBlockScope() {
        return blockScope;
    }

    public void setBlockScope(BlockScope blockScope) {
        this.blockScope = blockScope;
    }
}
