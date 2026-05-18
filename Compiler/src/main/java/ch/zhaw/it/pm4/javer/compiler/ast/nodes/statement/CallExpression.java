package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

/**
 * Expression node representing a function call and its arguments.
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class CallExpression extends ExpressionAstNodeBase {

    private final String functionName;
    private final List<ExpressionAstNode> arguments;
    private FunctionEntry resolvedFunction;

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

    public FunctionEntry getResolvedFunction() {
        return resolvedFunction;
    }

    public void setResolvedFunction(FunctionEntry resolvedFunction) {
        this.resolvedFunction = resolvedFunction;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
