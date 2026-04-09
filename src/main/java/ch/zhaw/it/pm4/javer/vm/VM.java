package ch.zhaw.it.pm4.javer.vm;

public class VM {
    private BytecodeLoader bytecodeLoader;
    private VMState vmState = new VMState();

    public VM(BytecodeLoader bytecodeLoader) {
        this.bytecodeLoader = bytecodeLoader;
    }
}
