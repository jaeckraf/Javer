package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

public final class VoidTypeInfo implements TypeInfo {

    public static final VoidTypeInfo INSTANCE = new VoidTypeInfo();

    private VoidTypeInfo() {
    }

    @Override
    public int sizeBytes() {
        return 0;
    }

    @Override
    public String toString() {
        return "void";
    }
}
