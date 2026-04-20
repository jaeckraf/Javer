package ch.zhaw.it.pm4.javer.compiler.ast;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;

public class VariableSymbolTableEntry extends SymbolTableEntry {
    private final ExpressionAstNode initializer;

    public VariableSymbolTableEntry(String name, TypeAstNode type, ExpressionAstNode initializer) {
        super(name, type);
        this.initializer = initializer;
    }

    public ExpressionAstNode getInitializer() {
        return initializer;
    }
}