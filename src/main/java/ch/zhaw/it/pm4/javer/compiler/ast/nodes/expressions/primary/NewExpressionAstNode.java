package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.ArrayInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class NewExpressionAstNode extends ExpressionAstNode {

    private final TypeAstNode type;
    private final List<ExpressionAstNode> arrayDimensions;
    private ArrayInitAstNode arrayInitializer;

    // need factory
    public NewExpressionAstNode(TypeAstNode type, List<ExpressionAstNode> arrayDimensions, ArrayInitAstNode arrayInitializer) {
        this.type = type;
        this.arrayDimensions = arrayDimensions;
        this.arrayInitializer = arrayInitializer;
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
