package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class FunctionDeclaration implements DeclarationAstNode {

    private final TypeAstNode returnType;
    private final String name;
    private final List<FunctionParameter> parameters;
    private final BlockStatement body;

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
