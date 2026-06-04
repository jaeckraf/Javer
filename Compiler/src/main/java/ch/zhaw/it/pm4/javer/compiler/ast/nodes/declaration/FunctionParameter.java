package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * AST node for a declared function parameter.
 */
public final class FunctionParameter extends AstNodeBase implements AstNode {

    private final String name;
    private final TypeAstNode type;
    private final boolean variadic;

    public FunctionParameter(String name, TypeAstNode type, boolean variadic) {
        this.name = name;
        this.type = type;
        this.variadic = variadic;
    }

    public TypeAstNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isVariadic() {
        return variadic;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
