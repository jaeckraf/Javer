package ch.zhaw.it.pm4.javer.compiler.bytecode;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;

/**
 * Centralizes the byte layout expected by the VM bytecode target.
 */
public final class VmLayout {

    public static final int BYTE_BYTES = 1;
    public static final int CHAR_BYTES = 2;
    public static final int WORD_BYTES = 4;
    public static final int DOUBLE_BYTES = 8;
    public static final int FRAME_HEADER_BYTES = 16;

    private VmLayout() {
    }

    public static int stackBytes(TypeInfo type) {
        return PrimitiveTypeInfo.DOUBLE.equals(type) ? DOUBLE_BYTES : WORD_BYTES;
    }

    public static int returnBytes(TypeInfo type) {
        if (type == null || type instanceof VoidTypeInfo) {
            return 0;
        }
        return stackBytes(type);
    }

    public static MemoryWidth memoryWidth(TypeInfo type) {
        if (type instanceof PrimitiveTypeInfo(PrimitiveTypeKind kind)) {
            return switch (kind) {
                case BOOL -> MemoryWidth.BYTE;
                case CHAR -> MemoryWidth.CHAR;
                case DOUBLE -> MemoryWidth.DOUBLE;
                case INT, STRING, INVALID -> MemoryWidth.WORD;
            };
        }
        return MemoryWidth.WORD;
    }

    public enum MemoryWidth {
        BYTE(BYTE_BYTES, "LOAD1", "STORE1"),
        CHAR(CHAR_BYTES, "LOAD2", "STORE2"),
        WORD(WORD_BYTES, "LOAD4", "STORE4"),
        DOUBLE(DOUBLE_BYTES, "LOAD8", "STORE8");

        private final int bytes;
        private final String loadInstruction;
        private final String storeInstruction;

        MemoryWidth(int bytes, String loadInstruction, String storeInstruction) {
            this.bytes = bytes;
            this.loadInstruction = loadInstruction;
            this.storeInstruction = storeInstruction;
        }

        public int bytes() {
            return bytes;
        }

        public String loadInstruction() {
            return loadInstruction;
        }

        public String storeInstruction() {
            return storeInstruction;
        }
    }
}
