package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

/**
 * Performs semantic checks that depend on control-flow context, such as
 * validating break and continue placement.
 */
@JacocoGenerated("jacoco-ignore")
public class SemanticChecker extends AstNodeVisitorBase {

    private final DiagnosticBag diagnosticBag;
    private int loopDepth = 0;

    /**
     * Creates a semantic-checking pass.
     *
     * @param diagnosticBag collector for semantic diagnostics
     */
    public SemanticChecker(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        node.getDeclarations().forEach(d -> d.accept(this));
    }

    @Override
    public void visit(FunctionDeclaration node) {
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
    }

    @Override
    public void visit(BlockStatement node) {
        node.getStatements().forEach(s -> s.accept(this));
    }

    @Override
    public void visit(IfStatement node) {
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        loopDepth++;
        node.getBody().accept(this);
        loopDepth--;
    }

    @Override
    public void visit(DoWhileStatement node) {
        loopDepth++;
        node.getBody().accept(this);
        loopDepth--;
    }

    @Override
    public void visit(ForStatement node) {
        loopDepth++;
        node.getBody().accept(this);
        loopDepth--;
    }

    @Override
    public void visit(SwitchStatement node) {
        node.getCases().forEach(c -> c.accept(this));
    }

    @Override
    public void visit(SwitchCase node) {
        if (node.getStatement() != null) {
            node.getStatement().accept(this);
        }
    }

    @Override
    public void visit(BreakStatement node) {
        if (loopDepth == 0) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "'break' used outside of a loop");
        }
    }

    @Override
    public void visit(ContinueStatement node) {
        if (loopDepth == 0) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "'continue' used outside of a loop");
        }
    }
}
