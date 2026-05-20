package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.scope.FunctionScope;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;

/**
 * Symbol table entry for a function and its generated frame metadata.
 */
public final class FunctionEntry extends SymbolEntry {

    private TypeInfo returnType = UnknownTypeInfo.INSTANCE;
    private FunctionScope scope;
    private final String label;
    private int parameterBytes;
    private int localBytes;
    private int frameSizeBytes;
    private boolean builtIn;

    public FunctionEntry(String name) {
        this(name, "_" + name);
    }

    public FunctionEntry(String name, String label) {
        super(name);
        this.label = label;
    }

    public TypeInfo getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeInfo returnType) {
        this.returnType = returnType == null ? UnknownTypeInfo.INSTANCE : returnType;
    }

    public FunctionScope getScope() {
        return scope;
    }

    public void setScope(FunctionScope scope) {
        this.scope = scope;
    }

    public String getLabel() {
        return label;
    }

    public int getParameterBytes() {
        return parameterBytes;
    }

    public void setParameterBytes(int parameterBytes) {
        this.parameterBytes = parameterBytes;
    }

    public int getLocalBytes() {
        return localBytes;
    }

    public int allocateLocalBytes(int sizeBytes) {
        int offset = localBytes;
        localBytes += sizeBytes;
        frameSizeBytes = localBytes;
        return offset;
    }

    public int getFrameSizeBytes() {
        return frameSizeBytes;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }
}
