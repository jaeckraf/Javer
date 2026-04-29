package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class CompilationUnit extends AstNodeBase implements AstNode {
    private final SymbolTable symbolTable;
    private final List<DeclarationAstNode> declarations;

    public CompilationUnit(List<DeclarationAstNode> declarations) {
        this.declarations = declarations;
        symbolTable = new SymbolTable(null);
    }

    public List<DeclarationAstNode> getDeclarations() {
        return declarations;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
