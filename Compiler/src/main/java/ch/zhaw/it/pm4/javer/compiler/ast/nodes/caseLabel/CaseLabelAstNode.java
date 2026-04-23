package ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

public sealed interface CaseLabelAstNode extends AstNode
        permits LiteralCaseLabel, EnumCaseLabel {
}
