package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.SymbolTable;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class StructDeclaration extends AstNodeBase implements DeclarationAstNode {

     private final String name;
     private final List<StructField> fields;
     private SymbolTable symbolTable;
     private StructSymbolTableEntry symbolEntry;

    public StructDeclaration(String name, List<StructField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<StructField> getFields() {
        return fields;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public StructSymbolTableEntry getSymbolEntry() {
        return symbolEntry;
    }

    public void setSymbolEntry(StructSymbolTableEntry symbolEntry) {
        this.symbolEntry = symbolEntry;
    }

    @Override
     public void accept(ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor visitor) {
        visitor.visit(this);
     }
}
