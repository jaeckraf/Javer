package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.binary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ternary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.unary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.types.*;

public interface ParseNodeVisitor<T> {

    T visit(BinaryExpressionParseNode node);

    T visit(ArrayInitParseNode node);

    T visit(VarInitParseNode node);

    T visit(CallExpressionParseNode node);

    T visit(EnumAccessExpressionParseNode node);

    T visit(IndexParseNode node);

    T visit(LiteralConstantParseNode node);

    T visit(NameAccessExpressionParseNode node);

    T visit(NewExpressionParseNode node);

    T visit(ParenthesizedExpressionParseNode node);

    T visit(ConditionalParseNode node);

    T visit(UnaryExpressionParseNode node);

    T visit(AssignmentParseNode node);

    T visit(ExpressionListParseNode node);

    T visit(PostfixParseNode node);

    T visit(BreakStmtParseNode node);

    T visit(ContinueStmtParseNode node);

    T visit(ReturnStmtParseNode node);

    T visit(CaseClauseParseNode node);

    T visit(DefaultClauseParseNode node);

    T visit(SwitchStmtParseNode node);

    T visit(IfStmtParseNode node);

    T visit(DoWhileStmtParseNode node);

    T visit(ForInitParseNode node);

    T visit(ForStmtParseNode node);

    T visit(ForUpdateParseNode node);

    T visit(WhileStmtParseNode node);

    T visit(BlockParseNode node);

    T visit(ExpressionStatementParseNode node);

    T visit(VarDeclarationParseNode node);

    T visit(EnumDeclarationParseNode node);

    T visit(EnumItemParseNode node);

    T visit(FunctionParameterParseNode node);

    T visit(FunctionParseNode node);

    T visit(StructDeclarationParseNode node);

    T visit(StructItemParseNode node);

    T visit(ArrayTypeParseNode node);

    T visit(AtomicTypeParseNode node);

    T visit(EnumTypeParseNode node);

    T visit(StructTypeParseNode node);

    T visit(VoidTypeParseNode node);

    T visit(CompilationUnitParseNode node);

}
