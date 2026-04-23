package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BytecodeLoaderTest {

    @Test
    void constructorInitializesDefaultInstructionAndStaticSegment() {
        BytecodeLoader loader = new BytecodeLoader("ignored-path");

        List<Instruction> instructions = loader.getInstructions();
        assertEquals(1, instructions.size());
        assertEquals(OPCode.HALT, instructions.getFirst().getOperationCode());

        MemorySegment staticSegment = loader.getStaticSegement();
        assertNotNull(staticSegment);
    }
}

