package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class ReturnStatement implements StatementAstNode {

    private ExpressionAstNode expression;

    public ReturnStatement(ExpressionAstNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
