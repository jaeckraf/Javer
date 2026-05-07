package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.ParameterSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class FunctionParameter extends AstNodeBase implements AstNode {

    private final String name;
    private final TypeAstNode type;
    private ParameterSymbolTableEntry symbolEntry;

    public FunctionParameter(String name, TypeAstNode type) {
        this.name = name;
        this.type = type;
    }

    public TypeAstNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ParameterSymbolTableEntry getSymbolEntry() {
        return symbolEntry;
    }

    public void setSymbolEntry(ParameterSymbolTableEntry symbolEntry) {
        this.symbolEntry = symbolEntry;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
