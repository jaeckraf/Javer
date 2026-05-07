package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class VariableSymbolTableEntry extends StorageSymbolTableEntry {
    private final boolean hasExplicitInitializer;
    private final Object defaultValue;

    private VariableSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.VARIABLE, builder.type, builder.sizeBytes, builder.offsetBytes);
        this.hasExplicitInitializer = builder.hasExplicitInitializer;
        this.defaultValue = builder.defaultValue;
    }

    public boolean hasExplicitInitializer() {
        return hasExplicitInitializer;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public static class Builder {
        private String name;
        private TypeAstNode type;
        private boolean hasExplicitInitializer;
        private Object defaultValue;
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

        public Builder hasExplicitInitializer(boolean hasExplicitInitializer) {
            this.hasExplicitInitializer = hasExplicitInitializer;
            return this;
        }

        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
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

        public VariableSymbolTableEntry build() {
            return new VariableSymbolTableEntry(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
