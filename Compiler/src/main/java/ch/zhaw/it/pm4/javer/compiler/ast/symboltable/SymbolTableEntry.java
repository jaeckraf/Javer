package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

public abstract class SymbolTableEntry {
    protected final String name;
    private final SymbolTableEntryKind kind;

    protected SymbolTableEntry(String name, SymbolTableEntryKind kind) {
        this.name = name;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public SymbolTableEntryKind getKind() {
        return kind;
    }
}
