package ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel;

import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public final class EnumCaseLabel implements CaseLabelAstNode {

    private final String enumTypeName;
    private final String enumValueName;

    public EnumCaseLabel(String enumTypeName, String enumValueName) {
        this.enumTypeName = enumTypeName;
        this.enumValueName = enumValueName;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
