package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionListAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class ForInitExpressionListAstNode extends ForInitAstNode {

    private final ExpressionListAstNode expressionList;

    public ForInitExpressionListAstNode(ExpressionListAstNode expressionList) {
        this.expressionList = expressionList;
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
