package ch.zhaw.it.pm4.javer.compiler.ast;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class VariableSymbolTableEntry extends SymbolTableEntry {
    private final ExpressionAstNode initializer;

    private VariableSymbolTableEntry(Builder builder) {
        super(builder.name, builder.type);
        this.initializer = builder.initializer;
    }

    public ExpressionAstNode getInitializer() {
        return initializer;
    }

    public static class Builder {
        private String name;
        private TypeAstNode type;
        private ExpressionAstNode initializer;

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

        public VariableSymbolTableEntry build() {
            return new VariableSymbolTableEntry(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}