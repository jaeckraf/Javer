package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.VarDecInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;


@JacocoGenerated("Dummy class, remove when implemented.")
public class VarDeclarationAstNode extends StatementAstNode {

    private final TypeAstNode type;
    private final String varName;
    private VarDecInitAstNode varDecInit;

    public VarDeclarationAstNode(String varName, TypeAstNode type, VarDecInitAstNode varDecInit) {
        this.varName = varName;
        this.type = type;
        this.varDecInit = varDecInit;
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
