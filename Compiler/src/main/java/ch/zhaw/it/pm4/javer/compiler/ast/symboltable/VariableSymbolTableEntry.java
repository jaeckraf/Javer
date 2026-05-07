package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class VariableSymbolTableEntry extends StorageSymbolTableEntry {
    private final ExpressionAstNode initializer;

    private VariableSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.VARIABLE, builder.type, builder.sizeBytes, builder.offsetBytes);
        this.initializer = builder.initializer;
    }

    public ExpressionAstNode getInitializer() {
        return initializer;
    }

    public static class Builder {
        private String name;
        private TypeAstNode type;
        private ExpressionAstNode initializer;
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

        public Builder initializer(ExpressionAstNode initializer) {
            this.initializer = initializer;
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
