package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;

public record EnumTypeInfo(EnumEntry entry) implements TypeInfo {

    @Override
    public int sizeBytes() {
        return entry == null ? 4 : entry.getElementSizeBytes();
    }

    @Override
    public String toString() {
        return "enum " + (entry == null ? "<unknown>" : entry.getName());
    }
}
