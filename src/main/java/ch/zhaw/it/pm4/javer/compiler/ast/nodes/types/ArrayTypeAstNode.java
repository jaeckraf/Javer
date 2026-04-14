package ch.zhaw.it.pm4.javer.compiler.ast.nodes.types;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class ArrayTypeAstNode extends TypeAstNode {

    private final TypeAstNode baseType;
    private int dimensions = -1; // default value for now

    public ArrayTypeAstNode(TypeAstNode baseType, int dimensions) {
        this.baseType = baseType;
        this.dimensions = dimensions;
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
