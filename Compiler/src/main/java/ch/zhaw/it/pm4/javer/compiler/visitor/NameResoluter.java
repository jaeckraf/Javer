package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class NameResoluter extends AstNodeVisitorBase {
    private SymbolTable currentScope;
    private final DiagnosticBag diagnosticBag;

    public NameResoluter(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        currentScope = node.getSymbolTable();

        super.visit(node);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        currentScope = node.getSymbolTable();

        super.visit(node);

        currentScope = currentScope.getParent(); // exit function scope
    }

    @Override
    public void visit(BlockStatement node) {
        currentScope = node.getSymbolTable();
        
        super.visit(node);

        currentScope = currentScope.getParent(); // exit block scope
    }

    @Override
    public void visit(NameExpression node) {
        if (!currentScope.contains(node.getName()))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined symbol: " + node.getName());
    }

    @Override
    public void visit(MemberAccessExpression node) {
        node.getTarget().accept(this);
        if (!(node.getTarget() instanceof NameExpression target))
            return;

        SymbolTableEntry entry = currentScope.getEntry(target.getName());

        if (!(entry instanceof EnumSymbolTableEntry enumEntry)) {
            diagnosticBag.add(node.getSourceRange().start(),
                Severity.ERROR,
                "Type is not an enum: " + target.getName());
            return;
        }

        if (!enumEntry.hasItem(node.getMemberName())) {
            diagnosticBag.add(node.getSourceRange().start(),
                Severity.ERROR,
                "Enum has no value: " + node.getMemberName());
        }
    }
}
