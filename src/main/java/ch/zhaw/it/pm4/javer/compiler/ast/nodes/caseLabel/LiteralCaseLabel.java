package ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class LiteralCaseLabel implements CaseLabelAstNode {

    private final LiteralExpression<?> literal;

    public LiteralCaseLabel(LiteralExpression<?> literal) {
        this.literal = literal;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
