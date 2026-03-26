package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.AssignmentParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ExpressionParseNode;
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

public abstract class ParseNodeVisitorBase<T> implements ParseNodeVisitor<T> {

    protected T visitDefault(ParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(BinaryExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ArrayInitParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(VarInitParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CallExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EnumAccessExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(IndexParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(LiteralConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(NameAccessExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ParenthesizedExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ConditionalParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(UnaryExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(AssignmentParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(PostfixParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(BreakStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ContinueStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ReturnStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CaseClauseParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(DefaultClauseParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(SwitchStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(IfStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(DoWhileStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ForInitParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ForStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ForUpdateParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(WhileStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(BlockParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ExpressionStatementParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(VarDeclarationParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EnumDeclarationParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EnumItemParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(FunctionParameterParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(FunctionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(StructDeclarationParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(StructItemParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ArrayTypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(AtomicTypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EnumTypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(StructTypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(VoidTypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CompilationUnitParseNode node) {
        return null;
    }

}
