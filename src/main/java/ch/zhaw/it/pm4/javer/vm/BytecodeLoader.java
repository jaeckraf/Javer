package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;

import java.io.File;
import java.util.List;

public class BytecodeLoader {

    private List<Instruction> instructions;
    private MemorySegment staticSegement;

    /**
     * TODO get data from file and set the instructions and segment
     * @param path
     */
    public BytecodeLoader(String path) {
        instructions = List.of(
                new Instruction(OPCode.HALT)
        );
        staticSegement = new MemorySegment();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public MemorySegment getStaticSegement() {
        return staticSegement;
    }
}
