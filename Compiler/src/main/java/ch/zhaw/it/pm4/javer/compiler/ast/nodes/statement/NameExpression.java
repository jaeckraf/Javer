package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.SymbolEntry;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class NameExpression extends ExpressionAstNodeBase {

    private final String name;

    private SymbolEntry symbolEntry;

    public NameExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public void setSymbolEntry(SymbolEntry symbolEntry) {
        this.symbolEntry = symbolEntry;
    }

    public SymbolEntry getSymbolEntry() {
        return symbolEntry;
    }
}
