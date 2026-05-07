package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;

public class EnumSymbolTableEntry extends SymbolTableEntry {

    private final List<EnumItem> items;
    private SymbolTable symbolTable;
    private final String dataLabel;
    private final int elementSizeBytes;
    private int sizeBytes;

    private EnumSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.ENUM);
        this.items = builder.items;
        this.symbolTable = builder.symbolTable;
        this.dataLabel = builder.dataLabel != null ? builder.dataLabel : "enum_" + builder.name;
        this.elementSizeBytes = builder.elementSizeBytes;
        this.sizeBytes = builder.sizeBytes;
    }

    public boolean hasItem(String itemName) {
        return symbolTable != null
            ? symbolTable.resolveEnumValue(itemName) != null
            : items.stream().anyMatch(item -> item.getName().equals(itemName));
    }

    public List<EnumItem> getItems() {
        return items;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public int getElementSizeBytes() {
        return elementSizeBytes;
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
        private List<EnumItem> items = List.of();
        private SymbolTable symbolTable;
        private String dataLabel;
        private int elementSizeBytes = 4;
        private int sizeBytes;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder items(List<EnumItem> items) {
            this.items = items;
            return this;
        }

        public Builder symbolTable(SymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }

        public Builder dataLabel(String dataLabel) {
            this.dataLabel = dataLabel;
            return this;
        }

        public Builder elementSizeBytes(int elementSizeBytes) {
            this.elementSizeBytes = elementSizeBytes;
            return this;
        }

        public Builder sizeBytes(int sizeBytes) {
            this.sizeBytes = sizeBytes;
            return this;
        }

        public EnumSymbolTableEntry build() {
            return new EnumSymbolTableEntry(this);
        }
    }
}
