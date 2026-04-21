package ch.zhaw.it.pm4.javer.vm;

import ch.zhaw.it.pm4.javer.vm.memorySegments.Heap;
import ch.zhaw.it.pm4.javer.vm.memorySegments.MemorySegment;

import java.util.List;

public class VM {

    private BytecodeLoader bytecodeLoader;

    public VM(BytecodeLoader bytecodeLoader) {
        this.bytecodeLoader = bytecodeLoader;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java VM <bytecode-file>");
            System.exit(1);
        }
        String bytecodeFile = args[0];
        BytecodeLoader bytecodeLoader = new BytecodeLoader(bytecodeFile);
        VM vm = new VM(bytecodeLoader);
        String result = vm.run();
        System.out.println(result);
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
               case OPCode.HALT -> {
                   isRunning = false;
                   // TODO implement add
               }
               default -> {
                     // TODO implement add
                   isRunning = false;
                   return new VMError("Unknown OPCode: " + instruction.getOperationCode(), programCounter, instruction).toString();
               }

           }

       }
        return "Successfully executed program";

    }

}
