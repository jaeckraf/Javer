package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
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
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class VisitorDispatchTestParser implements ParseNodeVisitor<String> {


    /**
     * @param node
     * @return
     */
    @Override
    public String visit(BinaryExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ArrayInitParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(VarInitParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(CallExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(EnumAccessExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(IndexParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(LiteralConstantParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(NameAccessExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(NewExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ParenthesizedExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ConditionalParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(UnaryExpressionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(AssignmentParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ExpressionListParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(PostfixParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(BreakStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ContinueStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ReturnStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(CaseClauseParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(DefaultClauseParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(SwitchStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(IfStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(DoWhileStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ForInitParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ForStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ForUpdateParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(WhileStmtParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(BlockParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ExpressionStatementParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(VarDeclarationParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(EnumDeclarationParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(EnumItemParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(FunctionParameterParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(FunctionParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(StructDeclarationParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(StructItemParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(ArrayTypeParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(AtomicTypeParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(EnumTypeParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(StructTypeParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(VoidTypeParseNode node) {
        return "";
    }

    /**
     * @param node
     * @return
     */
    @Override
    public String visit(CompilationUnitParseNode node) {
        return "";
    }
}
