package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ForInitExpressionList implements ForInit {

    private final List<ExpressionAstNode> expressions;

    public ForInitExpressionList(List<ExpressionAstNode> expressions) {
        this.expressions = expressions;
    }

    public List<ExpressionAstNode> getExpressions() {
        return expressions;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
