package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class FunctionSymbolTableEntry extends SymbolTableEntry {

    private final List<FunctionParameter> parameters;
    protected final TypeAstNode returnType;
    private final SymbolTable scope;

    private FunctionSymbolTableEntry(Builder builder) {
        super(builder.name);
        this.returnType = builder.returnType;
        this.parameters = builder.parameters;
        this.scope = builder.scope;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public TypeAstNode getReturnType() {
        return returnType;
    }

    public SymbolTable getScope() {
        return scope;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private TypeAstNode returnType;
        private List<FunctionParameter> parameters = List.of();
        private SymbolTable scope;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder returnType(TypeAstNode returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder parameters(List<FunctionParameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder scope(SymbolTable scope) {
            this.scope = scope;
            return this;
        }

        public FunctionSymbolTableEntry build() {
            return new FunctionSymbolTableEntry(this);
        }
    }
}