package ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class LiteralCaseLabel implements CaseLabelAstNode {

    private final LiteralExpression<?> literal;

    public LiteralCaseLabel(LiteralExpression<?> literal) {
        this.literal = literal;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
