package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class MemberAccessExpression implements ExpressionAstNode {

    private final ExpressionAstNode target;
    private final String memberName;

    public MemberAccessExpression(ExpressionAstNode target, String memberName) {
        this.target = target;
        this.memberName = memberName;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
