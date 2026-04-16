package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.DataSegment;
import ch.zhaw.it.pm4.javer.vm.memorySegments.Heap;
import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;

import java.util.List;

public class VM {

    private BytecodeLoader bytecodeLoader;

    public VM(BytecodeLoader bytecodeLoader) {
        this.bytecodeLoader = bytecodeLoader;
    }

    public String run() {
       int programCounter = 0;
       boolean isRunning = true;
       MemorySegment staticSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment argumentSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment localSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment pointerSegment; // TODO sind im Moment Dummies (MemorySegment)
       Heap heap;

       while(isRunning) {
           Instruction instruction = bytecodeLoader.getInstructions().get(programCounter);
           List<Operand> operands = instruction.getOperands();
           switch (instruction.getOperationCode()) {
               case HALT -> {
                   isRunning = false;
                   // TODO implement add
               }
               case WHILE_START ->
                   // Dummy: Always skip to WHILE_END
                   programCounter = findMatchingEnd(programCounter, OPCode.WHILE_END);

               case WHILE_END ->
                   // Dummy: Jump back to WHILE_START
                   programCounter = findMatchingStart(programCounter, OPCode.WHILE_START);

               case FOR_START ->
                   // Dummy: Always skip to FOR_END
                   programCounter = findMatchingEnd(programCounter, OPCode.FOR_END);

               case FOR_END ->
                   // Dummy: Jump back to FOR_START
                   programCounter = findMatchingStart(programCounter, OPCode.FOR_START);

               default -> {
                     // TODO implement add
                   isRunning = false;
                   return new VMError("Unknown OPCode: " + instruction.getOperationCode(), programCounter, instruction).toString();
               }

           }

       }
        return "Successfully executed program";

    }

    private int findMatchingEnd(int start, OPCode endCode) {
        int depth = 1;
        for (int i = start + 1; i < bytecodeLoader.getInstructions().size(); i++) {
            OPCode code = bytecodeLoader.getInstructions().get(i).getOperationCode();
            if (code == endCode) depth--;
            if (code == OPCode.WHILE_START || code == OPCode.FOR_START) depth++;
            if (depth == 0) return i;
        }
        throw new IllegalStateException("Matching end not found for " + endCode);
    }

    private int findMatchingStart(int end, OPCode startCode) {
        int depth = 1;
        for (int i = end - 1; i >= 0; i--) {
            OPCode code = bytecodeLoader.getInstructions().get(i).getOperationCode();
            if (code == startCode) depth--;
            if (code == OPCode.WHILE_END || code == OPCode.FOR_END) depth++;
            if (depth == 0) return i;
        }
        throw new IllegalStateException("Matching start not found for " + startCode);
    }
}
