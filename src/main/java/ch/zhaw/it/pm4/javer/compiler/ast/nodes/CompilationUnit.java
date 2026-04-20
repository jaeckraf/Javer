package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class CompilationUnit implements AstNode {

    private final List<DeclarationAstNode> declarations;

    public CompilationUnit(List<DeclarationAstNode> declarations) {
        this.declarations = declarations;
    }

    public List<DeclarationAstNode> getDeclarations() {
        return declarations;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
