package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class MemberAccessExpression implements ExpressionAstNode {

    private final ExpressionAstNode target;
    private final String memberName;

    public MemberAccessExpression(ExpressionAstNode target, String memberName) {
        this.target = target;
        this.memberName = memberName;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
