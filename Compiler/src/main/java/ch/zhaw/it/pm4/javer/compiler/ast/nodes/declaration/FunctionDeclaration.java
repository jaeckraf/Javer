package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.scope.FunctionScope;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * AST node for a function declaration, including return type, parameters, and body.
 */
public final class FunctionDeclaration extends AstNodeBase implements DeclarationAstNode {

    private final TypeAstNode returnType;
    private final String name;
    private final List<FunctionParameter> parameters;
    private final BlockStatement body;

    private FunctionScope functionScope;
    private FunctionEntry symbolEntry;

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

    public FunctionScope getFunctionScope() {
        return functionScope;
    }

    public void setFunctionScope(FunctionScope functionScope) {
        this.functionScope = functionScope;
    }

    public FunctionEntry getSymbolEntry() {
        return symbolEntry;
    }

    public void setSymbolEntry(FunctionEntry symbolEntry) {
        this.symbolEntry = symbolEntry;
    }
}
