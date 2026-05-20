package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
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
        FunctionEntry function = node.getSymbolEntry();
        TypeInfo returnType = function.getReturnType();
        Completion completion = analyzeCompletion(node.getBody());
        if (!(returnType instanceof VoidTypeInfo) && completion == Completion.NORMAL) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR,
                    "Missing return statement for function: " + node.getName());
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

    private Completion analyzeCompletion(StatementAstNode statement) {
        if (statement instanceof ReturnStatement) {
            return Completion.RETURNS;
        }
        if (statement instanceof BlockStatement block) {
            return analyzeBlock(block);
        }
        if (statement instanceof IfStatement ifStatement) {
            return analyzeIf(ifStatement);
        }
        if (statement instanceof SwitchStatement switchStatement) {
            return analyzeSwitch(switchStatement);
        }
        if (statement instanceof WhileStatement whileStatement) {
            return analyzeWhile(whileStatement);
        }
        if (statement instanceof ForStatement forStatement) {
            return analyzeFor(forStatement);
        }
        if (statement instanceof DoWhileStatement doWhileStatement) {
            return analyzeDoWhile(doWhileStatement);
        }
        return Completion.NORMAL;
    }

    private Completion analyzeBlock(BlockStatement block) {
        for (StatementAstNode statement : block.getStatements()) {
            Completion completion = analyzeCompletion(statement);
            if (completion != Completion.NORMAL) {
                return completion;
            }
        }
        return Completion.NORMAL;
    }

    private Completion analyzeIf(IfStatement ifStatement) {
        if (ifStatement.getElseBranch() == null) {
            return Completion.NORMAL;
        }
        Completion thenCompletion = analyzeCompletion(ifStatement.getThenBranch());
        Completion elseCompletion = analyzeCompletion(ifStatement.getElseBranch());
        if (thenCompletion == Completion.NORMAL || elseCompletion == Completion.NORMAL) {
            return Completion.NORMAL;
        }
        if (thenCompletion == Completion.RETURNS && elseCompletion == Completion.RETURNS) {
            return Completion.RETURNS;
        }
        return Completion.DOES_NOT_COMPLETE;
    }

    private Completion analyzeSwitch(SwitchStatement switchStatement) {
        boolean hasDefault = false;
        for (SwitchCase switchCase : switchStatement.getCases()) {
            if (switchCase.isDefault()) {
                hasDefault = true;
            }
            if (switchCase.getStatement() == null || analyzeCompletion(switchCase.getStatement()) == Completion.NORMAL) {
                return Completion.NORMAL;
            }
        }
        return hasDefault ? Completion.RETURNS : Completion.NORMAL;
    }

    private Completion analyzeWhile(WhileStatement statement) {
        if (!isBooleanLiteralTrue(statement.getCondition()) || containsBreak(statement.getBody())) {
            return Completion.NORMAL;
        }
        Completion bodyCompletion = analyzeCompletion(statement.getBody());
        if (bodyCompletion == Completion.RETURNS) {
            return Completion.RETURNS;
        }
        if (bodyCompletion == Completion.NORMAL && containsReturn(statement.getBody())) {
            warnComplexInfiniteLoop(statement);
        }
        return Completion.DOES_NOT_COMPLETE;
    }

    private Completion analyzeFor(ForStatement statement) {
        if (statement.getCondition() != null || containsBreak(statement.getBody())) {
            return Completion.NORMAL;
        }
        Completion bodyCompletion = analyzeCompletion(statement.getBody());
        if (bodyCompletion == Completion.RETURNS) {
            return Completion.RETURNS;
        }
        if (bodyCompletion == Completion.NORMAL && containsReturn(statement.getBody())) {
            warnComplexInfiniteLoop(statement);
        }
        return Completion.DOES_NOT_COMPLETE;
    }

    private Completion analyzeDoWhile(DoWhileStatement statement) {
        if (!isBooleanLiteralTrue(statement.getCondition()) || containsBreak(statement.getBody())) {
            return Completion.NORMAL;
        }
        Completion bodyCompletion = analyzeCompletion(statement.getBody());
        if (bodyCompletion == Completion.RETURNS) {
            return Completion.RETURNS;
        }
        if (bodyCompletion == Completion.NORMAL && containsReturn(statement.getBody())) {
            warnComplexInfiniteLoop(statement);
        }
        return Completion.DOES_NOT_COMPLETE;
    }

    private boolean isBooleanLiteralTrue(ExpressionAstNode expression) {
        return expression instanceof LiteralExpression<?> literal
                && literal.getKind() == LiteralKind.BOOLEAN
                && Boolean.TRUE.equals(literal.getValue());
    }

    private boolean containsReturn(StatementAstNode statement) {
        if (statement instanceof ReturnStatement) {
            return true;
        }
        if (statement instanceof BlockStatement block) {
            return block.getStatements().stream().anyMatch(this::containsReturn);
        }
        if (statement instanceof IfStatement ifStatement) {
            return containsReturn(ifStatement.getThenBranch())
                    || (ifStatement.getElseBranch() != null && containsReturn(ifStatement.getElseBranch()));
        }
        if (statement instanceof SwitchStatement switchStatement) {
            return switchStatement.getCases().stream()
                    .map(SwitchCase::getStatement)
                    .anyMatch(caseStatement -> caseStatement != null && containsReturn(caseStatement));
        }
        if (statement instanceof WhileStatement whileStatement) {
            return containsReturn(whileStatement.getBody());
        }
        if (statement instanceof ForStatement forStatement) {
            return containsReturn(forStatement.getBody());
        }
        if (statement instanceof DoWhileStatement doWhileStatement) {
            return containsReturn(doWhileStatement.getBody());
        }
        return false;
    }

    private boolean containsBreak(StatementAstNode statement) {
        if (statement instanceof BreakStatement) {
            return true;
        }
        if (statement instanceof BlockStatement block) {
            return block.getStatements().stream().anyMatch(this::containsBreak);
        }
        if (statement instanceof IfStatement ifStatement) {
            return containsBreak(ifStatement.getThenBranch())
                    || (ifStatement.getElseBranch() != null && containsBreak(ifStatement.getElseBranch()));
        }
        if (statement instanceof SwitchStatement switchStatement) {
            return switchStatement.getCases().stream()
                    .map(SwitchCase::getStatement)
                    .anyMatch(caseStatement -> caseStatement != null && containsBreak(caseStatement));
        }
        return false;
    }

    private void warnComplexInfiniteLoop(StatementAstNode statement) {
        diagnosticBag.add(statement.getSourceRange().start(), Severity.WARNING,
                "Infinite loop contains conditional return; normal completion is impossible but return is not definite.");
    }

    private enum Completion {
        NORMAL,
        RETURNS,
        DOES_NOT_COMPLETE
    }
}
