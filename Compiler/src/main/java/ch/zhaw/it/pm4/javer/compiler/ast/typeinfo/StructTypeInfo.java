package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;

public record StructTypeInfo(StructEntry entry) implements TypeInfo {

    @Override
    public int sizeBytes() {
        return REFERENCE_SIZE_BYTES;
    }

    @Override
    public String toString() {
        return "struct " + (entry == null ? "<unknown>" : entry.getName());
    }
}
