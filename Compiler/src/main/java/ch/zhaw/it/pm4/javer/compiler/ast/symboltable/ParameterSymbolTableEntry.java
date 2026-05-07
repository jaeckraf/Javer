package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class ParameterSymbolTableEntry extends StorageSymbolTableEntry {
    private ParameterSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.PARAMETER, builder.type, builder.sizeBytes, builder.offsetBytes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private TypeAstNode type;
        private int sizeBytes;
        private int offsetBytes;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TypeAstNode type) {
            this.type = type;
            return this;
        }

        public Builder sizeBytes(int sizeBytes) {
            this.sizeBytes = sizeBytes;
            return this;
        }

        public Builder offsetBytes(int offsetBytes) {
            this.offsetBytes = offsetBytes;
            return this;
        }

        public ParameterSymbolTableEntry build() {
            return new ParameterSymbolTableEntry(this);
        }
    }
}
