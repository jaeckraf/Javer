package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class WhileStmtAstNode extends StatementAstNode {

    private final ExpressionAstNode condition;
    private final StatementAstNode statement;

    public WhileStmtAstNode(ExpressionAstNode condition, StatementAstNode statement) {
        this.condition = condition;
        this.statement = statement;
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
