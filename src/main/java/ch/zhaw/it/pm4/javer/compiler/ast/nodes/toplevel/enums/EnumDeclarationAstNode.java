package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.TopLevelAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class EnumDeclarationAstNode extends TopLevelAstNode {

    private final String name;
    private final List<EnumItemAstNode> members;

    public EnumDeclarationAstNode(String name, List<EnumItemAstNode> members) {
        this.name = name;
        this.members = members;
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
