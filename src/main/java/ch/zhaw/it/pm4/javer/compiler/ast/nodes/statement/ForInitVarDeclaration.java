package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class ForInitVarDeclaration implements ForInit {

    private final VarDeclarationStatement varDeclaration;

    public ForInitVarDeclaration(VarDeclarationStatement varDeclaration) {
        this.varDeclaration = varDeclaration;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
