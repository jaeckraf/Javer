package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class EnumItemAstNode extends AstNode {

    private final String name;
    private int value = -1; // default value if nothing is defined

    public EnumItemAstNode(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public EnumItemAstNode(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

