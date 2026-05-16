package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;

/**
 * Base symbol entry for values that occupy storage in memory or a stack frame.
 */
public abstract class StorageEntry extends SymbolEntry {

    private final TypeInfo type;
    private final int sizeBytes;
    private final int offsetBytes;

    protected StorageEntry(String name, TypeInfo type, int sizeBytes, int offsetBytes) {
        super(name);
        this.type = type == null ? UnknownTypeInfo.INSTANCE : type;
        this.sizeBytes = sizeBytes;
        this.offsetBytes = offsetBytes;
    }

    public TypeInfo getType() {
        return type;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public int getOffsetBytes() {
        return offsetBytes;
    }
}
