package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.DataSection;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.GlobalScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.SemanticContext;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * Root node of a parsed Javer program.
 *
 * <p>The compilation unit owns the top-level declarations and the semantic
 * structures that are populated by later compiler phases.</p>
 */
public final class CompilationUnit extends AstNodeBase implements AstNode {
    private final GlobalScope globalScope;
    private final DataSection dataSection;
    private final SemanticContext semanticContext;
    private final List<DeclarationAstNode> declarations;

    public CompilationUnit(List<DeclarationAstNode> declarations) {
        this.declarations = declarations;
        this.globalScope = new GlobalScope();
        this.dataSection = new DataSection();
        this.semanticContext = new SemanticContext(globalScope, dataSection);
    }

    public List<DeclarationAstNode> getDeclarations() {
        return declarations;
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    public DataSection getDataSection() {
        return dataSection;
    }

    public SemanticContext getSemanticContext() {
        return semanticContext;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
