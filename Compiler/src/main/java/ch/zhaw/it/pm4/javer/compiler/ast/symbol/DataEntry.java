package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;

/**
 * Symbol table entry for a static data constant.
 */
public final class DataEntry extends SymbolEntry {

    private final TypeInfo type;
    private final Object value;
    private final int elementSizeBytes;

    public DataEntry(String label, TypeInfo type, Object value) {
        this(label, type, value, Math.max((type == null ? UnknownTypeInfo.INSTANCE : type).sizeBytes(), 1));
    }

    public DataEntry(String label, TypeInfo type, Object value, int elementSizeBytes) {
        super(label);
        this.type = type == null ? UnknownTypeInfo.INSTANCE : type;
        this.value = value;
        this.elementSizeBytes = Math.max(elementSizeBytes, 1);
    }

    public String getLabel() {
        return getName();
    }

    public TypeInfo getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value instanceof List<?> values) {
            return "%s %d %s".formatted(getLabel(), elementSizeBytes, String.join(",", values.stream()
                    .map(String::valueOf)
                    .toList()));
        }
        return "%s %d %s".formatted(getLabel(), elementSizeBytes, value);
    }
}
