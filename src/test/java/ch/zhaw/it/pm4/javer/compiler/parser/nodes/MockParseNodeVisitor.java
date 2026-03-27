package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.binary.BinaryExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init.ArrayInitParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.init.VarInitParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.primary.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.ternary.ConditionalParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.expressions.unary.UnaryExpressionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.IfStmtParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.jumps.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.branching.switchcase.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements.loops.*;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums.EnumDeclarationParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums.EnumItemParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function.FunctionParameterParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.function.FunctionParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct.StructDeclarationParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.struct.StructItemParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.types.*;

/**
 * Mock visitor for testing accept() method invocations.
 * This visitor returns a simple boolean to track if visit methods were called.
 */
public class MockParseNodeVisitor implements ParseNodeVisitor<Boolean> {

    @Override
    public Boolean visit(BinaryExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ArrayInitParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(VarInitParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(CallExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(EnumAccessExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(IndexParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(LiteralConstantParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(NameAccessExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ParenthesizedExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ConditionalParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(UnaryExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(AssignmentParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ExpressionListParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(PostfixParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(BreakStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ContinueStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ReturnStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(BlockParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ExpressionStatementParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(VarDeclarationParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(IfStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(WhileStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(DoWhileStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ForStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ForInitParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ForUpdateParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(CaseClauseParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(DefaultClauseParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(SwitchStmtParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(NewExpressionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(EnumDeclarationParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(EnumItemParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(FunctionParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(FunctionParameterParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(StructDeclarationParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(StructItemParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(ArrayTypeParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(AtomicTypeParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(EnumTypeParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(StructTypeParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(VoidTypeParseNode node) {
        return true;
    }

    @Override
    public Boolean visit(CompilationUnitParseNode node) {
        return true;
    }
}

