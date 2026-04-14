package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;


@JacocoGenerated("Dummy class, remove when implemented.")
public class BlockAstNode extends StatementAstNode {

    private final List<StatementAstNode> statements;

    public BlockAstNode(List<StatementAstNode> statements) {
        this.statements = statements;
    }

    /**
     * {@inheritDoc}
     * */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
