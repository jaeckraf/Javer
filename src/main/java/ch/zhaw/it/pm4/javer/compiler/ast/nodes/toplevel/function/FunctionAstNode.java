package ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.BlockAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.TopLevelAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class FunctionAstNode extends TopLevelAstNode {

    private final TypeAstNode type;
    private final String name;
    private final List<FunctionParameterAstNode> parameters;
    private final BlockAstNode body;

    public FunctionAstNode(TypeAstNode type, String name, List<FunctionParameterAstNode> parameters, BlockAstNode body) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
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
