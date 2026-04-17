package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ExpressionAstNode;

public sealed interface StatementAstNode extends AstNode
        permits ExpressionAstNode,
        BlockStatement,
        BreakStatement, ContinueStatement,
        DoWhileStatement, ForStatement,
        IfStatement, ReturnStatement, SwitchStatement,
        VarDeclarationStatement, WhileStatement {

}
