package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.postfix;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class PostfixAstNode extends ExpressionAstNode {

    private final ExpressionAstNode primaryExpression;
    private List<AbstractPostFixOperatorAstNode> postFixOperators;
    private PostFixOperator postFixOperator;


    // add bob the builder
    public PostfixAstNode(ExpressionAstNode primaryExpression, List<AbstractPostFixOperatorAstNode> postFixOperators, PostFixOperator postFixOperator) {
        this.primaryExpression = primaryExpression;
        this.postFixOperators = postFixOperators;
        this.postFixOperator = postFixOperator;
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
