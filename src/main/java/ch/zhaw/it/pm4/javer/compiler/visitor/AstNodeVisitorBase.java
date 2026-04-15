package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.AssignmentAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionListAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.postfix.PostfixAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.binary.BinaryExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.ArrayInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.VarInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.literal.LiteralConstantAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ternary.ConditionalAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.unary.UnaryExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.BlockAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.ExpressionStatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.VarDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.IfStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps.BreakStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps.ContinueStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.jumps.ReturnStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.CaseClauseAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.DefaultClauseAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.branching.switchcase.SwitchStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop.ForInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop.ForStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.forloop.ForUpdateAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums.EnumDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums.EnumItemAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function.FunctionParameterAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function.FunctionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct.StructDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct.StructItemAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.*;

import java.util.Optional;

@JacocoGenerated("Dummy class, remove when implemented.")
public abstract class AstNodeVisitorBase<T> implements AstNodeVisitor<T> {

    protected Optional<T> visitDefault(AstNode node) {
        return Optional.empty();
    }

    public abstract T visit(BinaryExpressionAstNode node);

    public abstract T visit(ArrayInitAstNode node);

    public abstract T visit(VarInitAstNode node);

    public abstract T visit(CallExpressionAstNode node);

    public abstract T visit(EnumAccessExpressionAstNode node);

    public abstract T visit(IndexAstNode node);

    public abstract T visit(LiteralConstantAstNode node);

    public abstract T visit(NameAccessExpressionAstNode node);

    public abstract T visit(NewExpressionAstNode node);

    public abstract T visit(ParenthesizedExpressionAstNode node);

    public abstract T visit(ConditionalAstNode node);

    public abstract T visit(UnaryExpressionAstNode node);

    public abstract T visit(AssignmentAstNode node);

    public abstract T visit(ExpressionListAstNode node);

    public abstract T visit(PostfixAstNode node);

    public abstract T visit(BreakStmtAstNode node);

    public abstract T visit(ContinueStmtAstNode node);

    public abstract T visit(ReturnStmtAstNode node);

    public abstract T visit(CaseClauseAstNode node);

    public abstract T visit(DefaultClauseAstNode node);

    public abstract T visit(SwitchStmtAstNode node);

    public abstract T visit(IfStmtAstNode node);

    public abstract T visit(DoWhileStmtAstNode node);

    public abstract T visit(ForInitAstNode node);

    public abstract T visit(ForStmtAstNode node);

    public abstract T visit(ForUpdateAstNode node);

    public abstract T visit(WhileStmtAstNode node);

    public abstract T visit(BlockAstNode node);

    public abstract T visit(ExpressionStatementAstNode node);

    public abstract T visit(VarDeclarationAstNode node);

    public abstract T visit(EnumDeclarationAstNode node);

    public abstract T visit(EnumItemAstNode node);

    public abstract T visit(FunctionParameterAstNode node);

    public abstract T visit(FunctionAstNode node);

    public abstract T visit(StructDeclarationAstNode node);

    public abstract T visit(StructItemAstNode node);

    public abstract T visit(ArrayTypeAstNode node);

    public abstract T visit(AtomicTypeAstNode node);

    public abstract T visit(EnumTypeAstNode node);

    public abstract T visit(StructTypeAstNode node);

    public abstract T visit(VoidTypeAstNode node);

    public abstract T visit(CompilationUnit node);

}
