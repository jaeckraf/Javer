package ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class EnumCaseLabel extends AstNodeBase implements CaseLabelAstNode {

    private final String enumTypeName;
    private final String enumValueName;

    public EnumCaseLabel(String enumTypeName, String enumValueName) {
        this.enumTypeName = enumTypeName;
        this.enumValueName = enumValueName;
    }

    public String getEnumTypeName() {
        return enumTypeName;
    }

    public String getEnumValueName() {
        return enumValueName;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
