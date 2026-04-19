package ch.zhaw.it.pm4.javer.vm;

public class VMError {
    private final String message;
    private final int programCounter;
    private final Instruction instruction;

    public VMError(String message, int programCounter, Instruction instruction) {
        this.message = message;
        this.programCounter = programCounter;
        this.instruction = instruction;
    }

    public String getMessage() {
        return message;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "VMError{message='" + message + "', programCounter=" + programCounter
                + ", instruction=" + instruction.getOperationCode() + "}";
    }
}
