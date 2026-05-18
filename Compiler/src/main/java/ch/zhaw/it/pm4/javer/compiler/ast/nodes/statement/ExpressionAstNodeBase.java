package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNodeBase;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;

/**
 * Base class for expression nodes that carry their resolved result type.
 */
public abstract non-sealed class ExpressionAstNodeBase extends AstNodeBase implements ExpressionAstNode {

    private TypeInfo resultingType = UnknownTypeInfo.INSTANCE;

    @Override
    public TypeInfo getResultingType() {
        return resultingType;
    }

    @Override
    public void setResultingType(TypeInfo resultingType) {
        this.resultingType = resultingType == null ? UnknownTypeInfo.INSTANCE : resultingType;
    }
}
