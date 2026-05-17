package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumValueEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FieldEntry;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * Expression node representing access to a member on a receiver expression.
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class MemberAccessExpression extends ExpressionAstNodeBase {

    private final ExpressionAstNode target;
    private final String memberName;
    private int value;
    private FieldEntry resolvedField;
    private EnumValueEntry resolvedEnumValue;

    public MemberAccessExpression(ExpressionAstNode target, String memberName) {
        this.target = target;
        this.memberName = memberName;
    }

    public ExpressionAstNode getTarget() {
        return target;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public FieldEntry getResolvedField() {
        return resolvedField;
    }

    public void setResolvedField(FieldEntry resolvedField) {
        this.resolvedField = resolvedField;
    }

    public EnumValueEntry getResolvedEnumValue() {
        return resolvedEnumValue;
    }

    public void setResolvedEnumValue(EnumValueEntry resolvedEnumValue) {
        this.resolvedEnumValue = resolvedEnumValue;
    }
}
