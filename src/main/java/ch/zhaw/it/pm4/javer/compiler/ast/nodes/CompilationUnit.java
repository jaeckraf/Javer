package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class CompilationUnit implements AstNode {

    private final List<DeclarationAstNode> declarations;

    public CompilationUnit(List<DeclarationAstNode> declarations) {
        this.declarations = declarations;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }

}
