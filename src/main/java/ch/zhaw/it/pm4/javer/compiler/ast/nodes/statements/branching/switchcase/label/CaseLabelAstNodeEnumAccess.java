package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.label;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.EnumAccessExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class CaseLabelAstNodeEnumAccess extends CaseLabelAstNode {

    private final EnumAccessExpressionAstNode enumAccessExpression;

    public CaseLabelAstNodeEnumAccess(EnumAccessExpressionAstNode enumAccessExpression) {
        this.enumAccessExpression = enumAccessExpression;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }

}
