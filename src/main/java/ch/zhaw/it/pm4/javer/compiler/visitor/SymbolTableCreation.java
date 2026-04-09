package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
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

public class SymbolTableCreation extends AstNodeVisitorBase<SymbolTable> {

    @Override
    public SymbolTable visit(BinaryExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ArrayInitAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(VarInitAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(CallExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(EnumAccessExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(IndexAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(LiteralConstantAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(NameAccessExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(NewExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ParenthesizedExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ConditionalAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(UnaryExpressionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(AssignmentAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ExpressionListAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(PostfixAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(BreakStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ContinueStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ReturnStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(CaseClauseAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(DefaultClauseAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(SwitchStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(IfStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(DoWhileStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ForInitAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ForStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ForUpdateAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(WhileStmtAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(BlockAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ExpressionStatementAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(VarDeclarationAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(EnumDeclarationAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(EnumItemAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(FunctionParameterAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(FunctionAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(StructDeclarationAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(StructItemAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(ArrayTypeAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(AtomicTypeAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(EnumTypeAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(StructTypeAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(VoidTypeAstNode node) {
        return null;
    }

    @Override
    public SymbolTable visit(CompilationUnitAstNode node) {
        return null;
    }
}
