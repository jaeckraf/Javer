package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;

public class StructSymbolTableEntry extends SymbolTableEntry {

    private final List<StructField> fields;

    private StructSymbolTableEntry(Builder builder) {
        super(builder.name);        
        this.fields = builder.fields;
    }

    public List<StructField> getFields() {
        return fields;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<StructField> fields = List.of();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fields(List<StructField> fields) {
            this.fields = fields;
            return this;
        }

        public StructSymbolTableEntry build() {
            return new StructSymbolTableEntry(this);
        }
    }
}