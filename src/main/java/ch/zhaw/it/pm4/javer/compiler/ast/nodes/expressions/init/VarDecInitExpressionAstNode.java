package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class VarDecInitExpressionAstNode extends VarDecInitAstNode {

    private final ExpressionAstNode expression;

    public VarDecInitExpressionAstNode(ExpressionAstNode expression) {
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     * */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
