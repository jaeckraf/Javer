package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class NewExpression implements ExpressionAstNode {

    private final TypeAstNode type;
    private final List<ExpressionAstNode> dimensions;
    private ArrayInitExpression arrayInit;

    public NewExpression(TypeAstNode type, List<ExpressionAstNode> dimensions, ArrayInitExpression arrayInit) {
        this.type = type;
        this.dimensions = dimensions;
        this.arrayInit = arrayInit;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
