package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.Arrays;
import java.util.List;

/**
 * Expression node representing allocation with {@code new}.
 */
public final class NewExpression extends ExpressionAstNodeBase {

    private final TypeAstNode type;
    private final List<ExpressionAstNode> dimensions;
    private final ArrayInitExpression arrayInit;
    private JaggedArrayTempLayout jaggedArrayTempLayout;

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

    public JaggedArrayTempLayout getJaggedArrayTempLayout() {
        return jaggedArrayTempLayout;
    }

    public void setJaggedArrayTempLayout(JaggedArrayTempLayout jaggedArrayTempLayout) {
        this.jaggedArrayTempLayout = jaggedArrayTempLayout;
    }

    public record JaggedArrayTempLayout(int[] dimensionOffsets, int[] baseOffsets, int[] indexOffsets) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JaggedArrayTempLayout(int[] offsets, int[] baseOffsets1, int[] indexOffsets1))) return false;

            return Arrays.equals(dimensionOffsets, offsets)
                    && Arrays.equals(baseOffsets, baseOffsets1)
                    && Arrays.equals(indexOffsets, indexOffsets1);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(dimensionOffsets);
            result = 31 * result + Arrays.hashCode(baseOffsets);
            result = 31 * result + Arrays.hashCode(indexOffsets);
            return result;
        }

        @Override
        public String toString() {
            return "JaggedArrayTempLayout{" +
                    "dimensionOffsets=" + Arrays.toString(dimensionOffsets) +
                    ", baseOffsets=" + Arrays.toString(baseOffsets) +
                    ", indexOffsets=" + Arrays.toString(indexOffsets) +
                    '}';
        }
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
