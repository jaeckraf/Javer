package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;

public sealed interface ExpressionAstNode extends AstNode, StatementAstNode
        permits ExpressionAstNodeBase {

    TypeInfo getResultingType();

    void setResultingType(TypeInfo resultingType);
}
