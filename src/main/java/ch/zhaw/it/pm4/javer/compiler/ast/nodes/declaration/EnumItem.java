package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class EnumItem implements AstNode {

    private final String name;
    private Integer value;

    public EnumItem(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
