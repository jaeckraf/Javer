package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceRange;

public abstract class AstNodeBase implements AstNode {

    private SourceRange sourceRange = SourceRange.UNKNOWN;

    @Override
    public SourceRange getSourceRange() {
        return sourceRange;
    }

    @Override
    public void setSourceRange(SourceRange sourceRange) {
        this.sourceRange = sourceRange;
    }
}
