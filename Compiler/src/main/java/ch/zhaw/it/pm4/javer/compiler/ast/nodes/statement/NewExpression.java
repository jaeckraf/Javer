package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class NewExpression implements ExpressionAstNode {

    private final TypeAstNode type;
    private final List<ExpressionAstNode> dimensions;
    private final ArrayInitExpression arrayInit;

    private NewExpression(Builder builder) {
        this.type = builder.type;
        this.dimensions = builder.dimensions;
        this.arrayInit = builder.arrayInit;
    }

    public static Builder builder(TypeAstNode type) {
        return new Builder(type);
    }

    public static final class Builder {
        private final TypeAstNode type;
        private List<ExpressionAstNode> dimensions = List.of();
        private ArrayInitExpression arrayInit;

        private Builder(TypeAstNode type) {
            this.type = type;
        }

        public Builder dimensions(List<ExpressionAstNode> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder arrayInit(ArrayInitExpression arrayInit) {
            this.arrayInit = arrayInit;
            return this;
        }

        public NewExpression build() {
            return new NewExpression(this);
        }
    }

    public TypeAstNode getType() {
        return type;
    }

    public List<ExpressionAstNode> getDimensions() {
        return dimensions;
    }

    public ArrayInitExpression getArrayInit() {
        return arrayInit;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
