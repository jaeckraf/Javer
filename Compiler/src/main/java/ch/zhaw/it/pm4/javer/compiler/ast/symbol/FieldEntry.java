package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;

public final class FieldEntry extends StorageEntry {

    public FieldEntry(String name, TypeInfo type, int sizeBytes, int offsetBytes) {
        super(name, type, sizeBytes, offsetBytes);
    }
}
