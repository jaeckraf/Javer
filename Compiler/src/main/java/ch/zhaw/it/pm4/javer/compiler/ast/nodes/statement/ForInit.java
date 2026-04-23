package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

public sealed interface ForInit extends AstNode
        permits ForInitVarDeclaration, ForInitExpressionList {
}
