package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.StructScope;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;

import java.util.List;

/**
 * AST node for a struct declaration and its fields.
 */
public final class StructDeclaration extends AstNodeBase implements DeclarationAstNode {

    private final String name;
    private final List<StructField> fields;
    private StructScope structScope;
    private StructEntry symbolEntry;

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

    public StructScope getStructScope() {
        return structScope;
    }

    public void setStructScope(StructScope structScope) {
        this.structScope = structScope;
    }

    public StructEntry getSymbolEntry() {
        return symbolEntry;
    }

    public void setSymbolEntry(StructEntry symbolEntry) {
        this.symbolEntry = symbolEntry;
    }

    @Override
    public void accept(ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
