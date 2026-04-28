package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

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

    public BlockStatement getBody() {
        return body;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public TypeAstNode getReturnType() {
        return returnType;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
