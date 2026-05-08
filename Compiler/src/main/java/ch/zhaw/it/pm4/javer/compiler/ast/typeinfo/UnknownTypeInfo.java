package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

public final class UnknownTypeInfo implements TypeInfo {

    public static final UnknownTypeInfo INSTANCE = new UnknownTypeInfo();

    private UnknownTypeInfo() {
    }

    @Override
    public int sizeBytes() {
        return 0;
    }

    @Override
    public String toString() {
        return "<unknown>";
    }
}
