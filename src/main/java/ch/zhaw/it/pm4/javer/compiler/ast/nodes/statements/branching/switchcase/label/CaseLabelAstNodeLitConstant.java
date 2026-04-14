package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.label;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.literal.LiteralConstantAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class CaseLabelAstNodeLitConstant extends CaseLabelAstNode {

    private final LiteralConstantAstNode literalConstantAstNode;

    public CaseLabelAstNodeLitConstant(LiteralConstantAstNode literalConstantAstNode) {
        this.literalConstantAstNode = literalConstantAstNode;
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
