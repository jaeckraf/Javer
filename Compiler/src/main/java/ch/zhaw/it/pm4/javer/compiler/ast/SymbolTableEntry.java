package ch.zhaw.it.pm4.javer.compiler.ast;

public abstract class SymbolTableEntry {
    protected final String name;

    protected SymbolTableEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
