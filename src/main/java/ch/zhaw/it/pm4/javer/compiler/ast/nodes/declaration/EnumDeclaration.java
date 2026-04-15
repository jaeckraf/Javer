package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class EnumDeclaration implements DeclarationAstNode {

    private final String name;
    private final List<EnumItem> items;

    public EnumDeclaration(String name, List<EnumItem> items) {
        this.name = name;
        this.items = items;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }

}
