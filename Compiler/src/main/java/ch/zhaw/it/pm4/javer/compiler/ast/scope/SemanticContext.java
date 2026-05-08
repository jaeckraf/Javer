package ch.zhaw.it.pm4.javer.compiler.ast.scope;

public final class SemanticContext {

    private final GlobalScope globalScope;
    private final DataSection dataSection;

    public SemanticContext(GlobalScope globalScope, DataSection dataSection) {
        this.globalScope = globalScope;
        this.dataSection = dataSection;
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    public DataSection getDataSection() {
        return dataSection;
    }
}
