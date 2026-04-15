package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

public sealed interface StatementAstNode extends AstNode
        permits BlockStatement, IfStatement, WhileStatement, DoWhileStatement, ForStatement,
        SwitchStatement, BreakStatement, ContinueStatement, ReturnStatement,
        VarDeclarationStatement {

}
