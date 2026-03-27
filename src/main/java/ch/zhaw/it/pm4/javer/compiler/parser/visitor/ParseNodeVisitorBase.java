package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.AssignmentParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ExpressionListParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.PostfixParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.binary.BinaryExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init.ArrayInitParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init.VarInitParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ternary.ConditionalParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.unary.UnaryExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.BlockParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.ExpressionStatementParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.VarDeclarationParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.IfStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps.BreakStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps.ContinueStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps.ReturnStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase.CaseClauseParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase.DefaultClauseParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase.SwitchStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums.EnumDeclarationParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums.EnumItemParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function.FunctionParameterParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function.FunctionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct.StructDeclarationParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct.StructItemParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.types.*;

import java.util.Optional;

@JacocoGenerated("Dummy class, remove when implemented.")
public abstract class ParseNodeVisitorBase<T> implements ParseNodeVisitor<T> {

    protected Optional<T> visitDefault(ParseNode node) {
        return Optional.empty();
    }

    public abstract T visit(BinaryExpressionParseNode node);

    public abstract T visit(ArrayInitParseNode node);

    public abstract T visit(VarInitParseNode node);

    public abstract T visit(CallExpressionParseNode node);

    public abstract T visit(EnumAccessExpressionParseNode node);

    public abstract T visit(IndexParseNode node);

    public abstract T visit(LiteralConstantParseNode node);

    public abstract T visit(NameAccessExpressionParseNode node);

    public abstract T visit(NewExpressionParseNode node);

    public abstract T visit(ParenthesizedExpressionParseNode node);

    public abstract T visit(ConditionalParseNode node);

    public abstract T visit(UnaryExpressionParseNode node);

    public abstract T visit(AssignmentParseNode node);

    public abstract T visit(ExpressionListParseNode node);

    public abstract T visit(PostfixParseNode node);

    public abstract T visit(BreakStmtParseNode node);

    public abstract T visit(ContinueStmtParseNode node);

    public abstract T visit(ReturnStmtParseNode node);

    public abstract T visit(CaseClauseParseNode node);

    public abstract T visit(DefaultClauseParseNode node);

    public abstract T visit(SwitchStmtParseNode node);

    public abstract T visit(IfStmtParseNode node);

    public abstract T visit(DoWhileStmtParseNode node);

    public abstract T visit(ForInitParseNode node);

    public abstract T visit(ForStmtParseNode node);

    public abstract T visit(ForUpdateParseNode node);

    public abstract T visit(WhileStmtParseNode node);

    public abstract T visit(BlockParseNode node);

    public abstract T visit(ExpressionStatementParseNode node);

    public abstract T visit(VarDeclarationParseNode node);

    public abstract T visit(EnumDeclarationParseNode node);

    public abstract T visit(EnumItemParseNode node);

    public abstract T visit(FunctionParameterParseNode node);

    public abstract T visit(FunctionParseNode node);

    public abstract T visit(StructDeclarationParseNode node);

    public abstract T visit(StructItemParseNode node);

    public abstract T visit(ArrayTypeParseNode node);

    public abstract T visit(AtomicTypeParseNode node);

    public abstract T visit(EnumTypeParseNode node);

    public abstract T visit(StructTypeParseNode node);

    public abstract T visit(VoidTypeParseNode node);

    public abstract T visit(CompilationUnitParseNode node);

}
