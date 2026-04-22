package ch.zhaw.it.pm4.javer.vm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class VM {

    private final byte[] stack = new byte[1048576]; // 1 MB
    private final byte[] data = new byte[1048576]; // 1 MB
    private final List<byte[]> heap = new ArrayList<>();
    private int sp = 0;
    private int pc = 0;
    private final HashMap<Integer, AbstractInstruction> code = new HashMap<>();
    private final List<String> lines;


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java VM <filePath>");
            return;
        }
        String filePath = args[0];
        try {
            VM vm = new VM(filePath);
            vm.run();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public VM(String filePath) throws IOException {
        this.lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.US_ASCII);
        for(String line : lines) {

        }


    }

    public void run() {

    }

    private static abstract class AbstractInstruction {
        public abstract void execute();
    }

    private static final class Instruction<U, V> extends AbstractInstruction {

        private final U op1;
        private final V op2;
        private final InstructionKind instructionKind;

        public Instruction(Builder<U, V> builder) {
            this.op1 = builder.op1;
            this.op2 = builder.op2;
            this.instructionKind = builder.instructionKind;
        }

        @Override
        public void execute() {

        }

        private static final class Builder<U, V> {
            private U op1;
            private V op2;
            private InstructionKind instructionKind;

            public Builder(InstructionKind instructionKind) {
                this.instructionKind = instructionKind;
            }

            public Builder<U, V> op1(U op1) {
                this.op1 = op1;
                return this;
            }

            public Builder<U, V> op2(V op2) {
                this.op2 = op2;
                return this;
            }

            public Instruction<U, V> build() {
                return new Instruction<>(this);
            }
        }
    }

    private static final class LabelInstruction extends AbstractInstruction {

        private final int address;

        public LabelInstruction(int address) {
            this.address = address;
        }

        @Override
        public void execute() {
        }
    }

    private enum InstructionKind {
        LOAD,
        STORE,
        PUSH,
        POP,
        HLOAD,
        HSTORE,
    }

}
