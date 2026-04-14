package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums.EnumDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function.FunctionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct.StructDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

@JacocoGenerated("Dummy class, remove when implemented.")
public class CompilationUnitAstNode extends AstNode {

    private final List<EnumDeclarationAstNode> enums;
    private final List<FunctionAstNode> functions;
    private final List<StructDeclarationAstNode> structs;

    public CompilationUnitAstNode(List<EnumDeclarationAstNode> enums, List<FunctionAstNode> functions, List<StructDeclarationAstNode> structs) {
        this.enums = enums;
        this.functions = functions;
        this.structs = structs;
    }

    /**
     * {@inheritDoc}
     *
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

