package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.*;

public interface ParseNodeVisitor<T> {

    T visit(CompilationUnitParseNode node);

    T visit(TopLevelParseNode node);

    T visit(EnumDeclarationParseNode node);

    T visit(EnumItemParseNode node);

    T visit(StructDeclarationParseNode node);

    T visit(StructFieldParseNode node);

    T visit(FunctionParseNode node);

    T visit(FParamParseNode node);

    T visit(TypeParseNode node);

    T visit(TypeHeadParseNode node);

    T visit(RTypeParseNode node);

    T visit(BlockParseNode node);

    T visit(StatementParseNode node);

    T visit(CheckParseNode node);

    T visit(IfStmtParseNode node);

    T visit(WhileStmtParseNode node);

    T visit(DoWhileStmtParseNode node);

    T visit(ForStmtParseNode node);

    T visit(ForInitParseNode node);

    T visit(ForUpdateParseNode node);

    T visit(SwitchStmtParseNode node);

    T visit(CaseParseNode node);

    T visit(CaseLabelParseNode node);

    T visit(EnumAccessParseNode node);

    T visit(JumpStmtParseNode node);

    T visit(VarDeclParseNode node);

    T visit(InitializerParseNode node);

    T visit(ArrayInitParseNode node);

    T visit(VarInitParseNode node);

    T visit(ExpressionListParseNode node);

    T visit(ExpressionParseNode node);

    T visit(AssignmentParseNode node);

    T visit(AssignOpParseNode node);

    T visit(ConditionalParseNode node);

    T visit(LogicalOrParseNode node);

    T visit(LogicalAndParseNode node);

    T visit(InclusiveOrParseNode node);

    T visit(ExclusiveOrParseNode node);

    T visit(AndExprParseNode node);

    T visit(EqExprParseNode node);

    T visit(RelExprParseNode node);

    T visit(ShiftExprParseNode node);

    T visit(AddExprParseNode node);

    T visit(MulExprParseNode node);

    T visit(UnaryExprParseNode node);

    T visit(PostfixParseNode node);

    T visit(PostfixOpParseNode node);

    T visit(IndexParseNode node);

    T visit(PrimaryParseNode node);

    T visit(NewExpressionParseNode node);

    T visit(LitConstantParseNode node);

    T visit(NumberConstantParseNode node);

    T visit(BooleanConstantParseNode node);

    T visit(StringConstantParseNode node);

    T visit(CharConstantParseNode node);

    T visit(RefConstantParseNode node);

}
