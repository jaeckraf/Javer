package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Dummy class, remove when implemented.")
public class FunctionParameterAstNode extends AstNode {

    private final TypeAstNode type;
    private final String name;

    public FunctionParameterAstNode(TypeAstNode type, String name) {
        this.type = type;
        this.name = name;
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

