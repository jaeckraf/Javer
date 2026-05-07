package ch.zhaw.it.pm4.javer.compiler.ast.nodes.type;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class NamedType extends AstNodeBase implements TypeAstNode {

    private final NameTypeKind kind;
    private final String name;
    private SymbolTableEntry resolvedEntry;

    public NamedType(NameTypeKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public NameTypeKind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public SymbolTableEntry getResolvedEntry() {
        return resolvedEntry;
    }

    public void setResolvedEntry(SymbolTableEntry resolvedEntry) {
        this.resolvedEntry = resolvedEntry;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
