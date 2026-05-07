package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class NameExpression extends AstNodeBase implements ExpressionAstNode {

    private final String name;
    private SymbolTableEntry resolvedEntry;

    public NameExpression(String name) {
        this.name = name;
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
