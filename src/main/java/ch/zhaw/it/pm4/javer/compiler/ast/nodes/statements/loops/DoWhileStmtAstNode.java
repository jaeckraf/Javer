package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.BlockAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class DoWhileStmtAstNode extends StatementAstNode {

    private final ExpressionAstNode expression;
    private final BlockAstNode block;

    public DoWhileStmtAstNode(ExpressionAstNode expression, BlockAstNode block) {
        this.expression = expression;
        this.block = block;
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
