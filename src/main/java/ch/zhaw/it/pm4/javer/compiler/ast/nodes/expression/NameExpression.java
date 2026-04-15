package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class NameExpression implements ExpressionAstNode {

    private final String name;

    public NameExpression(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
