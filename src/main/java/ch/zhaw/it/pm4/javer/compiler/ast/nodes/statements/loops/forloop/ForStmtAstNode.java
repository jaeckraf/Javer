package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class ForStmtAstNode extends StatementAstNode {

    private ForInitAstNode forInit;
    private ExpressionAstNode condition;
    private ForUpdateAstNode forUpdate;
    private final StatementAstNode statement;

    // lets add a builder for that

    public ForStmtAstNode(StatementAstNode statement, ForUpdateAstNode forUpdate, ExpressionAstNode condition, ForInitAstNode forInit) {
        this.statement = statement;
        this.forUpdate = forUpdate;
        this.condition = condition;
        this.forInit = forInit;
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
