package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class AtomicTypeAstNode extends TypeAstNode {

    private final AtomicTypes type;

    public AtomicTypeAstNode(AtomicTypes type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
      *
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
