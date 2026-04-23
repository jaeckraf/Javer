package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class FunctionDeclaration implements DeclarationAstNode {

    private final TypeAstNode returnType;
    private final String name;
    private final List<FunctionParameter> parameters;
    private final BlockStatement body;

    public FunctionDeclaration(TypeAstNode returnType, String name, List<FunctionParameter> parameters, BlockStatement body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public TypeAstNode getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
