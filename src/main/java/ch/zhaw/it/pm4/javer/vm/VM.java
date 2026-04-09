package ch.zhaw.it.pm4.javer.vm;

public class VM {
    private BytecodeLoader bytecodeLoader;
    private BytecodeModule bytecodeModule;
    private VMState vmState = new VMState();

    public VM(BytecodeLoader bytecodeLoader, BytecodeModule bytecodeModule) {
        this.bytecodeLoader = bytecodeLoader;
        this.bytecodeModule = bytecodeModule;
    }
}
