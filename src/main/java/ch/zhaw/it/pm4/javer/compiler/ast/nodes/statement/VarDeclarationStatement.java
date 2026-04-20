package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class VarDeclarationStatement implements StatementAstNode {

    private final TypeAstNode type;
    private final String name;
    private final ExpressionAstNode initializer;

    private VarDeclarationStatement(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.initializer = builder.initializer;
    }

    public static Builder builder(TypeAstNode type, String name) {
        return new Builder(type, name);
    }

    public static final class Builder {
        private final TypeAstNode type;
        private final String name;
        private ExpressionAstNode initializer;

        private Builder(TypeAstNode type, String name) {
            this.type = type;
            this.name = name;
        }

        public Builder initializer(ExpressionAstNode initializer) {
            this.initializer = initializer;
            return this;
        }

        public VarDeclarationStatement build() {
            return new VarDeclarationStatement(this);
        }
    }

    public TypeAstNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ExpressionAstNode getInitializer() {
        return initializer;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
