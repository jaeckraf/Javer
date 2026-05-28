package ch.zhaw.it.pm4.javer.compiler.bytecode;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.NullTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VmLayoutTest {

    @Test
    void stackBytesMatchVmOperandRepresentation() {
        assertAll(
                () -> assertEquals(4, VmLayout.stackBytes(PrimitiveTypeInfo.BOOL)),
                () -> assertEquals(4, VmLayout.stackBytes(PrimitiveTypeInfo.CHAR)),
                () -> assertEquals(4, VmLayout.stackBytes(PrimitiveTypeInfo.INT)),
                () -> assertEquals(4, VmLayout.stackBytes(PrimitiveTypeInfo.STRING)),
                () -> assertEquals(8, VmLayout.stackBytes(PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(4, VmLayout.stackBytes(NullTypeInfo.INSTANCE)),
                () -> assertEquals(4, VmLayout.stackBytes(new ArrayTypeInfo(PrimitiveTypeInfo.INT))),
                () -> assertEquals(4, VmLayout.stackBytes(new StructTypeInfo(new StructEntry("Point")))),
                () -> assertEquals(4, VmLayout.stackBytes(new EnumTypeInfo(new EnumEntry("Color"))))
        );
    }

    @Test
    void stackBytesRejectTypesWithoutOperandRepresentation() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.stackBytes(VoidTypeInfo.INSTANCE)),
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.stackBytes(UnknownTypeInfo.INSTANCE)),
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.stackBytes(PrimitiveTypeInfo.INVALID))
        );
    }

    @Test
    void returnBytesAreZeroForVoidAndOtherwiseUseStackBytes() {
        assertAll(
                () -> assertEquals(0, VmLayout.returnBytes(VoidTypeInfo.INSTANCE)),
                () -> assertEquals(4, VmLayout.returnBytes(PrimitiveTypeInfo.INT)),
                () -> assertEquals(8, VmLayout.returnBytes(PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(4, VmLayout.returnBytes(new ArrayTypeInfo(PrimitiveTypeInfo.CHAR)))
        );
    }

    @Test
    void memoryWidthMatchesTypeStorageWidth() {
        assertAll(
                () -> assertSame(VmLayout.MemoryWidth.BYTE, VmLayout.memoryWidth(PrimitiveTypeInfo.BOOL)),
                () -> assertSame(VmLayout.MemoryWidth.CHAR, VmLayout.memoryWidth(PrimitiveTypeInfo.CHAR)),
                () -> assertSame(VmLayout.MemoryWidth.WORD, VmLayout.memoryWidth(PrimitiveTypeInfo.INT)),
                () -> assertSame(VmLayout.MemoryWidth.WORD, VmLayout.memoryWidth(PrimitiveTypeInfo.STRING)),
                () -> assertSame(VmLayout.MemoryWidth.DOUBLE, VmLayout.memoryWidth(PrimitiveTypeInfo.DOUBLE)),
                () -> assertSame(VmLayout.MemoryWidth.WORD, VmLayout.memoryWidth(new ArrayTypeInfo(PrimitiveTypeInfo.INT))),
                () -> assertSame(VmLayout.MemoryWidth.WORD, VmLayout.memoryWidth(new StructTypeInfo(new StructEntry("Point")))),
                () -> assertSame(VmLayout.MemoryWidth.WORD, VmLayout.memoryWidth(new EnumTypeInfo(new EnumEntry("Color"))))
        );
    }

    @Test
    void memoryWidthRejectsTypesWithoutMemoryStorage() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.memoryWidth(VoidTypeInfo.INSTANCE)),
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.memoryWidth(NullTypeInfo.INSTANCE)),
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.memoryWidth(UnknownTypeInfo.INSTANCE)),
                () -> assertThrows(IllegalArgumentException.class, () -> VmLayout.memoryWidth(PrimitiveTypeInfo.INVALID))
        );
    }

    @Test
    void memoryWidthExposesLoadAndStoreInstructions() {
        assertAll(
                () -> assertEquals(1, VmLayout.MemoryWidth.BYTE.bytes()),
                () -> assertEquals("LOAD1", VmLayout.MemoryWidth.BYTE.loadInstruction()),
                () -> assertEquals("STORE1", VmLayout.MemoryWidth.BYTE.storeInstruction()),
                () -> assertEquals(2, VmLayout.MemoryWidth.CHAR.bytes()),
                () -> assertEquals("LOAD2", VmLayout.MemoryWidth.CHAR.loadInstruction()),
                () -> assertEquals("STORE2", VmLayout.MemoryWidth.CHAR.storeInstruction()),
                () -> assertEquals(4, VmLayout.MemoryWidth.WORD.bytes()),
                () -> assertEquals("LOAD4", VmLayout.MemoryWidth.WORD.loadInstruction()),
                () -> assertEquals("STORE4", VmLayout.MemoryWidth.WORD.storeInstruction()),
                () -> assertEquals(8, VmLayout.MemoryWidth.DOUBLE.bytes()),
                () -> assertEquals("LOAD8", VmLayout.MemoryWidth.DOUBLE.loadInstruction()),
                () -> assertEquals("STORE8", VmLayout.MemoryWidth.DOUBLE.storeInstruction())
        );
    }
}
