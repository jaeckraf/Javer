package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

public record ArrayTypeInfo(TypeInfo elementType) implements TypeInfo {

    public ArrayTypeInfo {
        if (elementType == null) {
            elementType = UnknownTypeInfo.INSTANCE;
        }
    }

    @Override
    public int sizeBytes() {
        return REFERENCE_SIZE_BYTES;
    }

    @Override
    public String toString() {
        return elementType + "[]";
    }
}
