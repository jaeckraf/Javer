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
    private final List<byte[]> heap = new ArrayList<>();
    private int sp = 0;
    private int pc = 0;
    private final HashMap<Integer, AbstractInstruction> code = new HashMap<>();
    private final List<String> lines;
    private final HashMap<String, byte[]> dataMap = new HashMap<>();


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
        parse();
    }

    private void parse() {
        int state = 0; // 0: none, 1: code, 2: data
        int address = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.equals("code:")) {
                state = 1;
                continue;
            }
            if (line.equals("data:")) {
                state = 2;
                continue;
            }
            if (state == 1) {
                // parse code
                if (line.matches("^_[a-zA-Z_][a-zA-Z0-9_]*:$")) {
                    // label
                    String name = line.substring(1, line.length() - 1);
                    code.put(address, new LabelInstruction(address));
                    // address not incremented for labels
                } else {
                    // instruction
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String instrName = parts[0].trim();
                        InstructionKind kind = getInstructionKind(instrName);
                        if (kind != null) {
                            Instruction.Builder<String, String> builder = new Instruction.Builder<>(kind);
                            String op1 = parts.length > 1 ? parts[1].trim() : null;
                            String op2 = parts.length > 2 ? parts[2].trim() : null;
                            if (op1 != null) builder.op1(op1);
                            if (op2 != null) builder.op2(op2);
                            Instruction<String, String> instr = builder.build();
                            code.put(address++, instr);
                        }
                    }
                }
            } else if (state == 2) {
                // parse data
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String name = parts[0];
                    int size = Integer.parseInt(parts[1]);
                    String[] hexes = parts[2].split(",");
                    List<Byte> byteList = new ArrayList<>();
                    for (String hex : hexes) {
                        hex = hex.trim();
                        long val = Long.parseLong(hex, 16);
                        for (int i = 0; i < size; i++) {
                            byteList.add((byte) (val >> (8 * i)));
                        }
                    }
                    byte[] dataBytes = new byte[byteList.size()];
                    for (int i = 0; i < byteList.size(); i++) {
                        dataBytes[i] = byteList.get(i);
                    }
                    dataMap.put(name, dataBytes);
                }
            }
        }
    }

    private InstructionKind getInstructionKind(String name) {
        return switch (name.toUpperCase()) {
            case "LOAD" -> InstructionKind.LOAD;
            case "STORE" -> InstructionKind.STORE;
            case "PUSH" -> InstructionKind.PUSH;
            case "POP" -> InstructionKind.POP;
            case "HLOAD" -> InstructionKind.HLOAD;
            case "HSTORE" -> InstructionKind.HSTORE;
            default -> null;
        };
    }

    public void run() {
        while (code.containsKey(pc)) {
            code.get(pc).execute(this);
            pc++;
        }
    }

    private static abstract class AbstractInstruction {
        public abstract void execute(VM vm);
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
        public void execute(VM vm) {
            // Implement instruction logic here, e.g., switch on instructionKind
            // Access vm.stack, vm.sp, etc.
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
        public void execute(VM vm) {
            // Labels do nothing for now
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
