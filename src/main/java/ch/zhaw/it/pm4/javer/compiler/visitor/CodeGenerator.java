package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.AssignmentAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.ExpressionListAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.PostfixAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.binary.BinaryExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.ArrayInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.init.VarInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.CallExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.EnumAccessExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.IndexAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.LiteralConstantAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.NameAccessExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.NewExpressionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.ParenthesizedExpressionAstNode;
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
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.DoWhileStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.ForInitAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.ForStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.ForUpdateAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statements.loops.WhileStmtAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums.EnumDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.enums.EnumItemAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function.FunctionAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.function.FunctionParameterAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct.StructDeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.toplevel.struct.StructItemAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.ArrayTypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.AtomicTypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.EnumTypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.StructTypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.types.VoidTypeAstNode;

/* TODO change generic/return type */
public class CodeGenerator extends AstNodeVisitorBase<Void> {
    @Override
    public Void visit(BinaryExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayInitAstNode node) {
        return null;
    }

    @Override
    public Void visit(VarInitAstNode node) {
        return null;
    }

    @Override
    public Void visit(CallExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(EnumAccessExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(IndexAstNode node) {
        return null;
    }

    @Override
    public Void visit(LiteralConstantAstNode node) {
        return null;
    }

    @Override
    public Void visit(NameAccessExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(NewExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(ParenthesizedExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(ConditionalAstNode node) {
        return null;
    }

    @Override
    public Void visit(UnaryExpressionAstNode node) {
        return null;
    }

    @Override
    public Void visit(AssignmentAstNode node) {
        return null;
    }

    @Override
    public Void visit(ExpressionListAstNode node) {
        return null;
    }

    @Override
    public Void visit(PostfixAstNode node) {
        return null;
    }

    @Override
    public Void visit(BreakStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(ContinueStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(ReturnStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(CaseClauseAstNode node) {
        return null;
    }

    @Override
    public Void visit(DefaultClauseAstNode node) {
        return null;
    }

    @Override
    public Void visit(SwitchStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(IfStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(DoWhileStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(ForInitAstNode node) {
        return null;
    }

    @Override
    public Void visit(ForStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(ForUpdateAstNode node) {
        return null;
    }

    @Override
    public Void visit(WhileStmtAstNode node) {
        return null;
    }

    @Override
    public Void visit(BlockAstNode node) {
        return null;
    }

    @Override
    public Void visit(ExpressionStatementAstNode node) {
        return null;
    }

    @Override
    public Void visit(VarDeclarationAstNode node) {
        return null;
    }

    @Override
    public Void visit(EnumDeclarationAstNode node) {
        return null;
    }

    @Override
    public Void visit(EnumItemAstNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionParameterAstNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionAstNode node) {
        return null;
    }

    @Override
    public Void visit(StructDeclarationAstNode node) {
        return null;
    }

    @Override
    public Void visit(StructItemAstNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayTypeAstNode node) {
        return null;
    }

    @Override
    public Void visit(AtomicTypeAstNode node) {
        return null;
    }

    @Override
    public Void visit(EnumTypeAstNode node) {
        return null;
    }

    @Override
    public Void visit(StructTypeAstNode node) {
        return null;
    }

    @Override
    public Void visit(VoidTypeAstNode node) {
        return null;
    }

    @Override
    public Void visit(CompilationUnitAstNode node) {
        return null;
    }
}
