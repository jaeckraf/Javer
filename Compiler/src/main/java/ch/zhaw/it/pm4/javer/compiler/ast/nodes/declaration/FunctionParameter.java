package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class FunctionParameter implements AstNode {

    private final String name;
    private final TypeAstNode type;

    public FunctionParameter(String name, TypeAstNode type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
