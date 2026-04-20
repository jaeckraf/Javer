package ch.zhaw.it.pm4.javer.compiler.ast;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public abstract class SymbolTableEntry {
    protected final String name;
    protected final TypeAstNode type;

    protected SymbolTableEntry(String name, TypeAstNode type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeAstNode getType() {
        return type;
    }
}
