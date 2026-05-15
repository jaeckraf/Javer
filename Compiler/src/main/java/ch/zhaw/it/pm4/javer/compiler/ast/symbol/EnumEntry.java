package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.scope.EnumScope;

/**
 * Symbol table entry for an enum declaration.
 */
public final class EnumEntry extends SymbolEntry {

    private EnumScope scope;
    private final String dataLabel;
    private final int elementSizeBytes;
    private int sizeBytes;

    public EnumEntry(String name) {
        this(name, "enum_" + name, 4);
    }

    public EnumEntry(String name, String dataLabel, int elementSizeBytes) {
        super(name);
        this.dataLabel = dataLabel;
        this.elementSizeBytes = elementSizeBytes;
    }

    public EnumScope getScope() {
        return scope;
    }

    public void setScope(EnumScope scope) {
        this.scope = scope;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public int getElementSizeBytes() {
        return elementSizeBytes;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}
