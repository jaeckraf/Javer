package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class EnumItem implements AstNode {

    private final String name;
    private final Integer value;

    private EnumItem(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private Integer value;

        private Builder(String name) {
            this.name = name;
        }

        public Builder value(Integer value) {
            this.value = value;
            return this;
        }

        public EnumItem build() {
            return new EnumItem(this);
        }
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
