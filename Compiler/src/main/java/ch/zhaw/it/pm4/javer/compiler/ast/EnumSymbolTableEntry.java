package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;

public class EnumSymbolTableEntry extends SymbolTableEntry {

    private final List<EnumItem> items;

    private EnumSymbolTableEntry(Builder builder) {
        super(builder.name);
        this.items = builder.items;
    }

    public List<EnumItem> getItems() {
        return items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<EnumItem> items = List.of();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder items(List<EnumItem> items) {
            this.items = items;
            return this;
        }

        public EnumSymbolTableEntry build() {
            return new EnumSymbolTableEntry(this);
        }
    }
}