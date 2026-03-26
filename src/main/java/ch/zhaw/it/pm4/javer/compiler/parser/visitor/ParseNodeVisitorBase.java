package ch.zhaw.it.pm4.javer.compiler.parser.visitor;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.*;

public abstract class ParseNodeVisitorBase<T> implements ParseNodeVisitor<T> {

    protected T visitDefault(ParseNode node) {
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

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(TopLevelParseNode node) {
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
    public T visit(StructDeclarationParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(StructFieldParseNode node) {
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
    public T visit(FParamParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(TypeParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(TypeHeadParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(RTypeParseNode node) {
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
    public T visit(StatementParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CheckParseNode node) {
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
    public T visit(WhileStmtParseNode node) {
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
    public T visit(ForStmtParseNode node) {
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
    public T visit(ForUpdateParseNode node) {
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
    public T visit(CaseParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CaseLabelParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EnumAccessParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(JumpStmtParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(VarDeclParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(InitializerParseNode node) {
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
    public T visit(ExpressionListParseNode node) {
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
    public T visit(AssignmentParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(AssignOpParseNode node) {
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
    public T visit(LogicalOrParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(LogicalAndParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(InclusiveOrParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ExclusiveOrParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(AndExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(EqExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(RelExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(ShiftExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(AddExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(MulExprParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(UnaryExprParseNode node) {
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
    public T visit(PostfixOpParseNode node) {
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
    public T visit(PrimaryParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(NewExpressionParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(LitConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(NumberConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(BooleanConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(StringConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(CharConstantParseNode node) {
        return null;
    }

    /**
     * @param node
     * @return
     */
    @Override
    public T visit(RefConstantParseNode node) {
        return null;
    }

}
