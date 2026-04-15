package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class CallExpression implements ExpressionAstNode {

    private final String functionName;
    private final List<ExpressionAstNode> arguments;

    public CallExpression(String functionName, List<ExpressionAstNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
