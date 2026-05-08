package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

public final class LabelEntry extends SymbolEntry {

    private final String label;

    public LabelEntry(String name, String label) {
        super(name);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
