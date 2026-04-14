package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class AssignmentAstNode extends ExpressionAstNode {

    private final ExpressionAstNode leftSideExpression;
    private AssignOperatorType assignOperatorType;
    private ExpressionAstNode rightSideExpression;


    // need builder for that
    public AssignmentAstNode(ExpressionAstNode leftSideExpression, AssignOperatorType assignOperatorType, ExpressionAstNode rightSideExpression) {
        this.leftSideExpression = leftSideExpression;
        this.assignOperatorType = assignOperatorType;
        this.rightSideExpression = rightSideExpression;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
