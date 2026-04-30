package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class EnumDeclaration extends AstNodeBase implements DeclarationAstNode {

    private final String name;
    private final List<EnumItem> items;

    public EnumDeclaration(String name, List<EnumItem> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<EnumItem> getItems() {
        return items;
    }


    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }

}
