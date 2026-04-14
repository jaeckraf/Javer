package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.VarDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class ForInitVarDeclAstNode extends ForInitAstNode {

    private final VarDeclarationAstNode varDecl;

    public ForInitVarDeclAstNode(VarDeclarationAstNode varDecl) {
        this.varDecl = varDecl;
    }

    /**
     * {@inheritDoc}
     * */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
