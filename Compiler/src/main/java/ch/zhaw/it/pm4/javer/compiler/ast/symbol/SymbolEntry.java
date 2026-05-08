package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

public abstract class SymbolEntry {

    private final String name;

    protected SymbolEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
