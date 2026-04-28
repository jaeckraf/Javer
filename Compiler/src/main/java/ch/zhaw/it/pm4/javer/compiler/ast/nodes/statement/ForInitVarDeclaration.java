package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ForInitVarDeclaration extends AstNodeBase implements ForInit {

    private final VarDeclarationStatement varDeclaration;

    public ForInitVarDeclaration(VarDeclarationStatement varDeclaration) {
        this.varDeclaration = varDeclaration;
    }

    public VarDeclarationStatement getVarDeclaration() {
        return varDeclaration;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
