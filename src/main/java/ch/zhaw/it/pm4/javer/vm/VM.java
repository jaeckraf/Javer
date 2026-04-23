package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.Heap;
import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;

import java.util.ArrayList;
import java.util.List;

public class VM {

    private final BytecodeLoader bytecodeLoader;

    public VM(BytecodeLoader bytecodeLoader) {
        this.bytecodeLoader = bytecodeLoader;
    }

    public String run() {
       int programCounter = 0;
       boolean isRunning = true;
       List<Instruction> instructions = bytecodeLoader.getInstructions();
       List<Object> stack = new ArrayList<>();
       MemorySegment staticSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment argumentSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment localSegment; // TODO sind im Moment Dummies (MemorySegment)
       MemorySegment pointerSegment; // TODO sind im Moment Dummies (MemorySegment)
       Heap heap;

       while (programCounter < instructions.size()) {
           Instruction instruction = instructions.get(programCounter);

           if (instruction.getOperationCode() == OPCode.PUSH) {
               List<Operand> operands = instruction.getOperands();
               if (operands.size() != 1) {
                   return new VMError("PUSH requires exactly one operand", programCounter, instruction).toString();
               }
               stack.add(operands.getFirst().getValue());
               programCounter++;
               continue;
           }

            if (instruction.getOperationCode() == OPCode.HALT || instruction.getOperationCode() == OPCode.RETURN) {
               return "Successfully executed program";
           }

           return new VMError("Unknown OPCode: " + instruction.getOperationCode(), programCounter, instruction).toString();
       }

       return new VMError("Program terminated without HALT", programCounter,
               new Instruction(OPCode.HALT)).toString();

    }

}
