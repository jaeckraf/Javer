package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceRange;

public interface AstNode {

    void accept(AstNodeVisitor visitor);

    SourceRange getSourceRange();

    void setSourceRange(SourceRange sourceRange);

}
