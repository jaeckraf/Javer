package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class FunctionSymbolTableEntry extends SymbolTableEntry {

    protected final TypeAstNode returnType;
    private SymbolTable symbolTable;
    private final String label;
    private int parameterBytes;
    private int localBytes;
    private int frameSizeBytes;

    private FunctionSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.FUNCTION);
        this.returnType = builder.returnType;
        this.symbolTable = builder.symbolTable;
        this.label = builder.label != null ? builder.label : "_" + builder.name;
    }

    public TypeAstNode getReturnType() {
        return returnType;
    }

    public SymbolTable getScope() {
        return symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String getLabel() {
        return label;
    }

    public int getParameterBytes() {
        return parameterBytes;
    }

    public void setParameterBytes(int parameterBytes) {
        this.parameterBytes = parameterBytes;
    }

    public int getLocalBytes() {
        return localBytes;
    }

    public int allocateLocalBytes(int sizeBytes) {
        int offset = localBytes;
        localBytes += sizeBytes;
        frameSizeBytes = localBytes;
        return offset;
    }

    public int getFrameSizeBytes() {
        return frameSizeBytes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private TypeAstNode returnType;
        private SymbolTable symbolTable;
        private String label;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder returnType(TypeAstNode returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder scope(SymbolTable scope) {
            this.symbolTable = scope;
            return this;
        }

        public Builder symbolTable(SymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public FunctionSymbolTableEntry build() {
            return new FunctionSymbolTableEntry(this);
        }
    }
}
