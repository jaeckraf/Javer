package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;

public final class TypeLayout {
    private TypeLayout() {
    }

    public static int sizeOf(TypeAstNode type) {
        if (type instanceof PrimitiveType primitiveType) {
            return switch (primitiveType.getKind()) {
                case BOOL -> 1;
                case CHAR -> 2;
                case INT -> 4;
                case DOUBLE -> 8;
                case STRING -> 4;
                case INVALID -> 0;
            };
        }
        if (type instanceof NamedType) {
            return 4;
        }
        if (type instanceof ArrayType) {
            return 4;
        }
        if (type instanceof VoidType) {
            return 0;
        }
        return 0;
    }

    public static Object defaultValueOf(TypeAstNode type) {
        if (type instanceof PrimitiveType primitiveType) {
            return switch (primitiveType.getKind()) {
                case BOOL -> false;
                case CHAR -> '\0';
                case INT -> 0;
                case DOUBLE -> 0.0;
                case STRING, INVALID -> null;
            };
        }
        return null;
    }
}
