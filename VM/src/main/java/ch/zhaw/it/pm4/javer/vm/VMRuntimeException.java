package ch.zhaw.it.pm4.javer.vm;

public class VMRuntimeException extends RuntimeException {
    private final int pc;

    public VMRuntimeException(int pc, String message) {
        super("Runtime Error at line " + (pc + 1) + ": " + message);
        this.pc = pc;
    }

    public int getPc() {
        return pc;
    }
}
