package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;

/**
 * Symbol table entry for a function parameter.
 */
public final class ParameterEntry extends StorageEntry {

    private final boolean variadic;
    private final TypeInfo variadicElementType;

    public ParameterEntry(String name, TypeInfo type, int sizeBytes, int offsetBytes) {
        this(name, type, sizeBytes, offsetBytes, false, null);
    }

    public ParameterEntry(
            String name,
            TypeInfo type,
            int sizeBytes,
            int offsetBytes,
            boolean variadic,
            TypeInfo variadicElementType) {
        super(name, type, sizeBytes, offsetBytes);
        this.variadic = variadic;
        this.variadicElementType = variadicElementType;
    }

    public boolean isVariadic() {
        return variadic;
    }

    public TypeInfo getVariadicElementType() {
        return variadicElementType;
    }
}
