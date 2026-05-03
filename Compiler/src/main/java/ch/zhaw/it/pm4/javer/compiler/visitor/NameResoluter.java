package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.ArrayDeque;
import java.util.Deque;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ArrayInitExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.AssignExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BinaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.CallExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ConditionalExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.DoWhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForInitExpressionList;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForInitVarDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IfStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IndexExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NewExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.PostfixExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ReturnStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchCase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.UnaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.WhileStatement;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

/**
 * Resolves every {@link NameExpression} in the AST to its {@link SymbolTableEntry}.
 *
 * Runs after {@link SymbolTableCreation}, which populates the {@link CompilationUnit}'s
 * top-level table with declarations. This pass walks the tree maintaining a scope stack
 * to handle nested function/block/for scopes, registers locals as it sees them, and
 * decorates each {@code NameExpression} with the entry it binds to. Unresolved names
 * are reported as {@link Severity#ERROR} on the diagnostic bag.
 */
@JacocoGenerated("jacoco-ignore")
public class NameResoluter extends AstNodeVisitorBase {

    private final DiagnosticBag diagnosticBag;
    private final Deque<SymbolTable> scopes = new ArrayDeque<>();

    public NameResoluter(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    private SymbolTable currentScope() {
        return scopes.peek();
    }

    private void pushScope() {
        scopes.push(new SymbolTable(currentScope()));
    }

    private void popScope() {
        scopes.pop();
    }


    @Override
    public void visit(CompilationUnit node) {
        scopes.push(node.getSymbolTable());
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        scopes.pop();
    }

    @Override
    public void visit(FunctionDeclaration node) {
        pushScope();
        for (FunctionParameter parameter : node.getParameters()) {
            VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
                    .name(parameter.getName())
                    .type(parameter.getType())
                    .build();
            currentScope().addEntry(entry, diagnosticBag);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        popScope();
    }

    // EnumDeclaration / StructDeclaration: their entries are already in the top-level
    // table from SymbolTableCreation; their bodies contain no NameExpression references
    // to resolve. Default visitor (no-op) is correct.


    @Override
    public void visit(BlockStatement node) {
        pushScope();
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }
        popScope();
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        // Visit initializer in the OUTER scope so `let int x = x;` does not self-resolve.
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
                .name(node.getName())
                .type(node.getType())
                .initializer(node.getInitializer())
                .build();
        currentScope().addEntry(entry, diagnosticBag);
    }

    @Override
    public void visit(IfStatement node) {
        node.getCondition().accept(this);
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        node.getCondition().accept(this);
        node.getBody().accept(this);
    }

    @Override
    public void visit(DoWhileStatement node) {
        node.getBody().accept(this);
        node.getCondition().accept(this);
    }

    @Override
    public void visit(ForStatement node) {
        pushScope();
        if (node.getForInit() != null) {
            node.getForInit().accept(this);
        }
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getUpdate() != null) {
            for (ExpressionAstNode update : node.getUpdate()) {
                update.accept(this);
            }
        }
        node.getBody().accept(this);
        popScope();
    }

    @Override
    public void visit(ForInitVarDeclaration node) {
        node.getVarDeclaration().accept(this);
    }

    @Override
    public void visit(ForInitExpressionList node) {
        for (ExpressionAstNode expression : node.getExpressions()) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(SwitchStatement node) {
        node.getCondition().accept(this);
        for (SwitchCase switchCase : node.getCases()) {
            switchCase.accept(this);
        }
    }

    @Override
    public void visit(SwitchCase node) {
        if (node.getStatement() != null) {
            node.getStatement().accept(this);
        }
    }

    @Override
    public void visit(ReturnStatement node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }

    // BreakStatement / ContinueStatement: no children. Default no-op is correct.

    @Override
    public void visit(NameExpression node) {
        SymbolTableEntry entry = currentScope().getEntry(node.getName());
        if (entry == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR,
                    "Undefined identifier '" + node.getName() + "'");
            return;
        }
        node.setResolvedEntry(entry);
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
    }

    @Override
    public void visit(UnaryExpression node) {
        node.getOperand().accept(this);
    }

    @Override
    public void visit(PostfixExpression node) {
        node.getOperand().accept(this);
    }

    @Override
    public void visit(AssignExpression node) {
        node.getTarget().accept(this);
        node.getValue().accept(this);
    }

    @Override
    public void visit(ConditionalExpression node) {
        node.getCondition().accept(this);
        node.getTrueExpression().accept(this);
        node.getFalseExpression().accept(this);
    }

    @Override
    public void visit(IndexExpression node) {
        node.getTarget().accept(this);
        node.getIndex().accept(this);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        // Recurse into target only. `memberName` is resolved against the struct/enum
        // type during the type-check phase, not here.
        node.getTarget().accept(this);
    }

    @Override
    public void visit(CallExpression node) {
        // Function-name resolution is its own follow-up ticket (the AST currently holds
        // the function name as a raw String, not a NameExpression). Resolve arguments.
        for (ExpressionAstNode argument : node.getArguments()) {
            argument.accept(this);
        }
    }

    @Override
    public void visit(NewExpression node) {
        for (ExpressionAstNode dimension : node.getDimensions()) {
            dimension.accept(this);
        }
        if (node.getArrayInit() != null) {
            node.getArrayInit().accept(this);
        }
    }

    @Override
    public void visit(ArrayInitExpression node) {
        for (ExpressionAstNode element : node.getElements()) {
            element.accept(this);
        }
    }

    // LiteralExpression: no child nodes; default no-op.
}
