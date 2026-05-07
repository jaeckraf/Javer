package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public abstract class StorageSymbolTableEntry extends SymbolTableEntry {
    private final TypeAstNode type;
    private final int sizeBytes;
    private final int offsetBytes;

    protected StorageSymbolTableEntry(
            String name,
            SymbolTableEntryKind kind,
            TypeAstNode type,
            int sizeBytes,
            int offsetBytes
    ) {
        super(name, kind);
        this.type = type;
        this.sizeBytes = sizeBytes;
        this.offsetBytes = offsetBytes;
    }

    public TypeAstNode getType() {
        return type;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public int getOffsetBytes() {
        return offsetBytes;
    }
}
