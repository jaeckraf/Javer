package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class VarDeclarationStatement implements StatementAstNode {

    private final TypeAstNode type;
    private final String name;
    private ExpressionAstNode initializer;

    public VarDeclarationStatement(TypeAstNode type, String name, ExpressionAstNode initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
