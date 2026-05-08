package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

public sealed interface TypeInfo
        permits PrimitiveTypeInfo, ArrayTypeInfo, StructTypeInfo, EnumTypeInfo, VoidTypeInfo, UnknownTypeInfo {

    int REFERENCE_SIZE_BYTES = 4;

    int sizeBytes();
}
