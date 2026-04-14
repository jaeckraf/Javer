package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.TopLevelAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class StructDeclarationAstNode extends TopLevelAstNode {

    private final String name;
    private List<StructItemAstNode> fields;

    public StructDeclarationAstNode(String name, List<StructItemAstNode> fields) {
        this.name = name;
        this.fields = fields;
    }

    /**
     * {@inheritDoc}
     * */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
