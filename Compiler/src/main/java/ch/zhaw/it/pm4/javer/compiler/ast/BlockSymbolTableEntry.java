package ch.zhaw.it.pm4.javer.compiler.ast;

public class BlockSymbolTableEntry extends SymbolTableEntry {
    private final SymbolTable symbolTable;
    
    public BlockSymbolTableEntry(SymbolTable symbolTable) {
        super("block");
        this.symbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setName(String name) {
        this.name = name;
    }
}