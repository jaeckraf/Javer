package ch.zhaw.it.pm4.javer.compiler.ast.symbol;

import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

public final class VariableEntry extends StorageEntry {

    private final boolean hasExplicitInitializer;
    private final Object defaultValue;
    private final SourceLocation visibilityStart;

    public VariableEntry(
            String name,
            TypeInfo type,
            int sizeBytes,
            int offsetBytes,
            boolean hasExplicitInitializer,
            Object defaultValue,
            SourceLocation visibilityStart) {
        super(name, type, sizeBytes, offsetBytes);
        this.hasExplicitInitializer = hasExplicitInitializer;
        this.defaultValue = defaultValue;
        this.visibilityStart = visibilityStart;
    }

    public boolean hasExplicitInitializer() {
        return hasExplicitInitializer;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public SourceLocation getVisibilityStart() {
        return visibilityStart;
    }
}
