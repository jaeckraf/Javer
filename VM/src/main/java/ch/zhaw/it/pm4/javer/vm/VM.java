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
    private final HashMap<String, Integer> labels = new HashMap<>();
    private int haltAddress = 0;


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
        // First pass: collect labels
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
            if (state == 1 && line.matches("^_[a-zA-Z_][a-zA-Z0-9_]*:$")) {
                String name = line.substring(1, line.length() - 1);
                labels.put(name, address);
            } else if (state == 1 && !line.matches("^_[a-zA-Z_][a-zA-Z0-9_]*:$")) {
                address++;
            }
        }
        haltAddress = address;
        
        // Second pass: parse code
        state = 0;
        address = 0;
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
                    code.put(address, new LabelInstruction(address));
                    // address not incremented for labels
                } else {
                    // instruction
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String instrName = parts[0].trim();
                        InstructionKind kind = getInstructionKind(instrName);
                        if (kind != null) {
                            Instruction.Builder<String> builder = new Instruction.Builder<>(kind);
                            String op1 = parts.length > 1 ? parts[1].trim() : null;
                            if (op1 != null) builder.op1(op1);
                            Instruction<String> instr = builder.build();
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
        // Add HALT instruction at the end
        code.put(haltAddress, new HaltInstruction());
    }

    private InstructionKind getInstructionKind(String name) {
        return switch (name.toUpperCase()) {
            // Stack operations
            case "PUSHB" -> InstructionKind.PUSHB;
            case "PUSHC" -> InstructionKind.PUSHC;
            case "PUSHI" -> InstructionKind.PUSHI;
            case "PUSHD" -> InstructionKind.PUSHD;
            case "POPB" -> InstructionKind.POPB;
            case "POPC" -> InstructionKind.POPC;
            case "POPI" -> InstructionKind.POPI;
            case "POPD" -> InstructionKind.POPD;
            // Heap operations
            case "HLOAD1" -> InstructionKind.HLOAD1;
            case "HLOAD2" -> InstructionKind.HLOAD2;
            case "HLOAD4" -> InstructionKind.HLOAD4;
            case "HLOAD8" -> InstructionKind.HLOAD8;
            case "HSTORE1" -> InstructionKind.HSTORE1;
            case "HSTORE2" -> InstructionKind.HSTORE2;
            case "HSTORE4" -> InstructionKind.HSTORE4;
            case "HSTORE8" -> InstructionKind.HSTORE8;
            case "NEW" -> InstructionKind.NEW;
            // Arithmetic
            case "IADD" -> InstructionKind.IADD;
            case "ISUB" -> InstructionKind.ISUB;
            case "IMUL" -> InstructionKind.IMUL;
            case "IDIV" -> InstructionKind.IDIV;
            case "IMOD" -> InstructionKind.IMOD;
            case "DADD" -> InstructionKind.DADD;
            case "DSUB" -> InstructionKind.DSUB;
            case "DMUL" -> InstructionKind.DMUL;
            case "DDIV" -> InstructionKind.DDIV;
            // Comparisons
            case "ILT" -> InstructionKind.ILT;
            case "ILE" -> InstructionKind.ILE;
            case "IGT" -> InstructionKind.IGT;
            case "IGE" -> InstructionKind.IGE;
            case "IEQ" -> InstructionKind.IEQ;
            case "INE" -> InstructionKind.INE;
            case "DLT" -> InstructionKind.DLT;
            case "DLE" -> InstructionKind.DLE;
            case "DGT" -> InstructionKind.DGT;
            case "DGE" -> InstructionKind.DGE;
            case "DEQ" -> InstructionKind.DEQ;
            case "DNE" -> InstructionKind.DNE;
            // Shifts
            case "ISHL" -> InstructionKind.ISHL;
            case "ISHR" -> InstructionKind.ISHR;
            // Bit operations
            case "IAND" -> InstructionKind.IAND;
            case "IOR" -> InstructionKind.IOR;
            case "IXOR" -> InstructionKind.IXOR;
            // Unary
            case "INEG" -> InstructionKind.INEG;
            case "DNEG" -> InstructionKind.DNEG;
            case "IINV" -> InstructionKind.IINV;
            // Jump instructions
            case "JUMP" -> InstructionKind.JUMP;
            case "JUMPT" -> InstructionKind.JUMPT;
            case "JUMPF" -> InstructionKind.JUMPF;
            default -> null;
        };
    }

    public void run() {
        while (code.containsKey(pc)) {
            code.get(pc).execute(this);
            pc++;
        }
    }

    // Helper methods for stack operations
    private void pushByte(byte val) {
        stack[sp++] = val;
    }

    private byte popByte() {
        return stack[--sp];
    }

    private void pushChar(char val) {
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
    }

    private char popChar() {
        byte high = stack[--sp];
        byte low = stack[--sp];
        return (char) ((high << 8) | (low & 0xFF));
    }

    private void pushInt(int val) {
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
        stack[sp++] = (byte) ((val >> 16) & 0xFF);
        stack[sp++] = (byte) ((val >> 24) & 0xFF);
    }

    private int popInt() {
        byte b3 = stack[--sp];
        byte b2 = stack[--sp];
        byte b1 = stack[--sp];
        byte b0 = stack[--sp];
        return (b3 << 24) | ((b2 & 0xFF) << 16) | ((b1 & 0xFF) << 8) | (b0 & 0xFF);
    }

    private void pushDouble(double val) {
        long bits = Double.doubleToLongBits(val);
        for (int i = 0; i < 8; i++) {
            stack[sp++] = (byte) (bits & 0xFF);
            bits >>= 8;
        }
    }

    private double popDouble() {
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits |= ((long) (stack[--sp] & 0xFF)) << (8 * i);
        }
        return Double.longBitsToDouble(bits);
    }

    private static abstract class AbstractInstruction {
        public abstract void execute(VM vm);
    }

    private static final class Instruction<U> extends AbstractInstruction {

        private final U op1;
        private final InstructionKind instructionKind;

        public Instruction(Builder<U> builder) {
            this.op1 = builder.op1;
            this.instructionKind = builder.instructionKind;
        }

        @Override
        public void execute(VM vm) {
            switch (instructionKind) {
                // Stack operations
                case PUSHB -> vm.pushByte(Byte.parseByte((String) op1));
                case PUSHC -> vm.pushChar((char) Integer.parseInt((String) op1));
                case PUSHI -> vm.pushInt(Integer.parseInt((String) op1));
                case PUSHD -> vm.pushDouble(Double.parseDouble((String) op1));
                case POPB -> vm.popByte();
                case POPC -> vm.popChar();
                case POPI -> vm.popInt();
                case POPD -> vm.popDouble();
                // Heap operations
                case HLOAD1 -> {
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    byte val = vm.heap.get(base)[offset];
                    vm.pushByte(val);
                }
                case HLOAD2 -> {
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    byte low = vm.heap.get(base)[offset];
                    byte high = vm.heap.get(base)[offset + 1];
                    char val = (char) ((high << 8) | (low & 0xFF));
                    vm.pushChar(val);
                }
                case HLOAD4 -> {
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    int val = 0;
                    for (int i = 0; i < 4; i++) {
                        val |= (vm.heap.get(base)[offset + i] & 0xFF) << (8 * i);
                    }
                    vm.pushInt(val);
                }
                case HLOAD8 -> {
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    long val = 0;
                    for (int i = 0; i < 8; i++) {
                        val |= ((long) (vm.heap.get(base)[offset + i] & 0xFF)) << (8 * i);
                    }
                    vm.pushDouble(Double.longBitsToDouble(val));
                }
                case HSTORE1 -> {
                    byte val = vm.popByte();
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    vm.heap.get(base)[offset] = val;
                }
                case HSTORE2 -> {
                    char val = vm.popChar();
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    vm.heap.get(base)[offset] = (byte) (val & 0xFF);
                    vm.heap.get(base)[offset + 1] = (byte) ((val >> 8) & 0xFF);
                }
                case HSTORE4 -> {
                    int val = vm.popInt();
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    for (int i = 0; i < 4; i++) {
                        vm.heap.get(base)[offset + i] = (byte) (val & 0xFF);
                        val >>= 8;
                    }
                }
                case HSTORE8 -> {
                    double dval = vm.popDouble();
                    long val = Double.doubleToLongBits(dval);
                    int offset = vm.popInt();
                    int base = vm.popInt();
                    for (int i = 0; i < 8; i++) {
                        vm.heap.get(base)[offset + i] = (byte) (val & 0xFF);
                        val >>= 8;
                    }
                }
                case NEW -> {
                    int size = vm.popInt();
                    vm.heap.add(new byte[size]);
                    vm.pushInt(vm.heap.size() - 1);
                }
                // Arithmetic
                case IADD -> vm.pushInt(vm.popInt() + vm.popInt());
                case ISUB -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushInt(a - b);
                }
                case IMUL -> vm.pushInt(vm.popInt() * vm.popInt());
                case IDIV -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    if (b == 0) {
                        System.err.println("Error: Division by zero");
                        vm.pc = vm.haltAddress - 1;
                    } else {
                        vm.pushInt(a / b);
                    }
                }
                case IMOD -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    if (b == 0) {
                        System.err.println("Error: Modulo by zero");
                        vm.pc = vm.haltAddress - 1;
                    } else {
                        vm.pushInt(a % b);
                    }
                }
                case DADD -> vm.pushDouble(vm.popDouble() + vm.popDouble());
                case DSUB -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    vm.pushDouble(a - b);
                }
                case DMUL -> vm.pushDouble(vm.popDouble() * vm.popDouble());
                case DDIV -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    if (b == 0.0) {
                        System.err.println("Error: Division by zero");
                        vm.pc = vm.haltAddress - 1;
                    } else {
                        vm.pushDouble(a / b);
                    }
                }
                // Comparisons
                case ILT -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushByte((byte) (a < b ? 1 : 0));
                }
                case ILE -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushByte((byte) (a <= b ? 1 : 0));
                }
                case IGT -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushByte((byte) (a > b ? 1 : 0));
                }
                case IGE -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushByte((byte) (a >= b ? 1 : 0));
                }
                case IEQ -> vm.pushByte((byte) (vm.popInt() == vm.popInt() ? 1 : 0));
                case INE -> vm.pushByte((byte) (vm.popInt() != vm.popInt() ? 1 : 0));
                case DLT -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    vm.pushByte((byte) (a < b ? 1 : 0));
                }
                case DLE -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    vm.pushByte((byte) (a <= b ? 1 : 0));
                }
                case DGT -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    vm.pushByte((byte) (a > b ? 1 : 0));
                }
                case DGE -> {
                    double b = vm.popDouble();
                    double a = vm.popDouble();
                    vm.pushByte((byte) (a >= b ? 1 : 0));
                }
                case DEQ -> vm.pushByte((byte) (vm.popDouble() == vm.popDouble() ? 1 : 0));
                case DNE -> vm.pushByte((byte) (vm.popDouble() != vm.popDouble() ? 1 : 0));
                // Shifts
                case ISHL -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushInt(a << b);
                }
                case ISHR -> {
                    int b = vm.popInt();
                    int a = vm.popInt();
                    vm.pushInt(a >> b);
                }
                // Bit operations
                case IAND -> vm.pushInt(vm.popInt() & vm.popInt());
                case IOR -> vm.pushInt(vm.popInt() | vm.popInt());
                case IXOR -> vm.pushInt(vm.popInt() ^ vm.popInt());
                // Unary
                case INEG -> vm.pushInt(-vm.popInt());
                case DNEG -> vm.pushDouble(-vm.popDouble());
                case IINV -> vm.pushInt(~vm.popInt());
                // Jump instructions
                case JUMP -> {
                    String labelName = (String) op1;
                    Integer target = vm.labels.get(labelName);
                    if (target == null) {
                        System.err.println("Error: Label '" + labelName + "' not found");
                        vm.pc = vm.haltAddress - 1;
                    } else {
                        vm.pc = target - 1;
                    }
                }
                case JUMPT -> {
                    byte condition = vm.popByte();
                    String labelName = (String) op1;
                    if (condition != 0) {
                        Integer target = vm.labels.get(labelName);
                        if (target == null) {
                            System.err.println("Error: Label '" + labelName + "' not found");
                            vm.pc = vm.haltAddress - 1;
                        } else {
                            vm.pc = target - 1;
                        }
                    }
                }
                case JUMPF -> {
                    byte condition = vm.popByte();
                    String labelName = (String) op1;
                    if (condition == 0) {
                        Integer target = vm.labels.get(labelName);
                        if (target == null) {
                            System.err.println("Error: Label '" + labelName + "' not found");
                            vm.pc = vm.haltAddress - 1;
                        } else {
                            vm.pc = target - 1;
                        }
                    }
                }
            }
        }

        private static final class Builder<U> {
            private U op1;
            private InstructionKind instructionKind;

            public Builder(InstructionKind instructionKind) {
                this.instructionKind = instructionKind;
            }

            public Builder<U> op1(U op1) {
                this.op1 = op1;
                return this;
            }

            public Instruction<U> build() {
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

    private static final class HaltInstruction extends AbstractInstruction {

        @Override
        public void execute(VM vm) {
            // HALT instruction does nothing, it's just a stop point
        }
    }

    private enum InstructionKind {
        // Stack operations
        PUSHB, PUSHC, PUSHI, PUSHD,
        POPB, POPC, POPI, POPD,
        // Heap operations
        HLOAD1, HLOAD2, HLOAD4, HLOAD8,
        HSTORE1, HSTORE2, HSTORE4, HSTORE8,
        NEW,
        // Arithmetic
        IADD, ISUB, IMUL, IDIV, IMOD,
        DADD, DSUB, DMUL, DDIV,
        // Comparisons
        ILT, ILE, IGT, IGE, IEQ, INE,
        DLT, DLE, DGT, DGE, DEQ, DNE,
        // Shifts
        ISHL, ISHR,
        // Bit operations
        IAND, IOR, IXOR,
        // Unary
        INEG, DNEG, IINV,
        // Jump instructions
        JUMP, JUMPT, JUMPF,
    }

}
