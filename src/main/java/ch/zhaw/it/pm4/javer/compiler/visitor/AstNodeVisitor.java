package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnitAstNode;
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

public interface AstNodeVisitor<T> {

    T visit(BinaryExpressionAstNode node);

    T visit(ArrayInitAstNode node);

    T visit(VarInitAstNode node);

    T visit(CallExpressionAstNode node);

    T visit(EnumAccessExpressionAstNode node);

    T visit(IndexAstNode node);

    T visit(LiteralConstantAstNode node);

    T visit(NameAccessExpressionAstNode node);

    T visit(NewExpressionAstNode node);

    T visit(ParenthesizedExpressionAstNode node);

    T visit(ConditionalAstNode node);

    T visit(UnaryExpressionAstNode node);

    T visit(AssignmentAstNode node);

    T visit(ExpressionListAstNode node);

    T visit(PostfixAstNode node);

    T visit(BreakStmtAstNode node);

    T visit(ContinueStmtAstNode node);

    T visit(ReturnStmtAstNode node);

    T visit(CaseClauseAstNode node);

    T visit(DefaultClauseAstNode node);

    T visit(SwitchStmtAstNode node);

    T visit(IfStmtAstNode node);

    T visit(DoWhileStmtAstNode node);

    T visit(ForInitAstNode node);

    T visit(ForStmtAstNode node);

    T visit(ForUpdateAstNode node);

    T visit(WhileStmtAstNode node);

    T visit(BlockAstNode node);

    T visit(ExpressionStatementAstNode node);

    T visit(VarDeclarationAstNode node);

    T visit(EnumDeclarationAstNode node);

    T visit(EnumItemAstNode node);

    T visit(FunctionParameterAstNode node);

    T visit(FunctionAstNode node);

    T visit(StructDeclarationAstNode node);

    T visit(StructItemAstNode node);

    T visit(ArrayTypeAstNode node);

    T visit(AtomicTypeAstNode node);

    T visit(EnumTypeAstNode node);

    T visit(StructTypeAstNode node);

    T visit(VoidTypeAstNode node);

    T visit(CompilationUnitAstNode node);

}
