package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;

/**
 * Symbol table entry for a function parameter.
 */
public final class ParameterEntry extends StorageEntry {

    public ParameterEntry(String name, TypeInfo type, int sizeBytes, int offsetBytes) {
        super(name, type, sizeBytes, offsetBytes);
    }
}
