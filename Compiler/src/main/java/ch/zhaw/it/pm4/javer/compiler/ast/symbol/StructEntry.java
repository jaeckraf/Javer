package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.scope.StructScope;

public final class StructEntry extends SymbolEntry {

    private StructScope scope;

    public StructEntry(String name) {
        super(name);
    }

    public StructScope getScope() {
        return scope;
    }

    public void setScope(StructScope scope) {
        this.scope = scope;
    }

    public int getSizeBytes() {
        return scope == null ? 0 : scope.getSizeBytes();
    }
}
