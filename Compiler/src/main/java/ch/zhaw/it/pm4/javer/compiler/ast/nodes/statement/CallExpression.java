package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class CallExpression extends AstNodeBase implements ExpressionAstNode {

    private final String functionName;
    private final List<ExpressionAstNode> arguments;

    public CallExpression(String functionName, List<ExpressionAstNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ExpressionAstNode> getArguments() {
        return arguments;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
