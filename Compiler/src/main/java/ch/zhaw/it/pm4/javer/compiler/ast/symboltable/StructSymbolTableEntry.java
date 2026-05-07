package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

public class StructSymbolTableEntry extends SymbolTableEntry {

    private SymbolTable symbolTable;
    private int sizeBytes;

    private StructSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.STRUCT);
        this.symbolTable = builder.symbolTable;
        this.sizeBytes = builder.sizeBytes;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private SymbolTable symbolTable;
        private int sizeBytes;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder symbolTable(SymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }

        public Builder sizeBytes(int sizeBytes) {
            this.sizeBytes = sizeBytes;
            return this;
        }

        public StructSymbolTableEntry build() {
            return new StructSymbolTableEntry(this);
        }
    }
}
