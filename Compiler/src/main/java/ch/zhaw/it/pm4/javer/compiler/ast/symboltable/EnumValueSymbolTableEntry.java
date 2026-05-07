package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

public class EnumValueSymbolTableEntry extends SymbolTableEntry {
    private final EnumSymbolTableEntry ownerEnum;
    private final int value;
    private final int sizeBytes;
    private final int offsetBytes;
    private final String dataLabel;

    private EnumValueSymbolTableEntry(Builder builder) {
        super(builder.name, SymbolTableEntryKind.ENUM_VALUE);
        this.ownerEnum = builder.ownerEnum;
        this.value = builder.value;
        this.sizeBytes = builder.sizeBytes;
        this.offsetBytes = builder.offsetBytes;
        this.dataLabel = builder.dataLabel;
    }

    public EnumSymbolTableEntry getOwnerEnum() {
        return ownerEnum;
    }

    public int getValue() {
        return value;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public int getOffsetBytes() {
        return offsetBytes;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private EnumSymbolTableEntry ownerEnum;
        private int value;
        private int sizeBytes = 4;
        private int offsetBytes;
        private String dataLabel;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerEnum(EnumSymbolTableEntry ownerEnum) {
            this.ownerEnum = ownerEnum;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
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

        public Builder dataLabel(String dataLabel) {
            this.dataLabel = dataLabel;
            return this;
        }

        public EnumValueSymbolTableEntry build() {
            return new EnumValueSymbolTableEntry(this);
        }
    }
}
