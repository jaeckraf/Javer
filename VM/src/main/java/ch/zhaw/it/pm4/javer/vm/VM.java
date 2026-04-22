package ch.zhaw.it.pm4.javer.vm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VM {

    private final byte[] stack = new byte[1048576]; // 1 MB
    private final List<byte[]> heap = new ArrayList<>();
    private int sp = 0;
    private int pc = 0;

    private final Map<Integer, Instruction> code = new HashMap<>();
    private final List<String> lines;
    private final Map<String, byte[]> dataMap = new HashMap<>();
    private final Map<String, Integer> labels = new HashMap<>();

    private int programEndAddress = 0;
    private boolean halted = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java VM <filePath>");
            return;
        }

        String filePath = args[0];

        try {
            VM vm = new VM(filePath);
            vm.run();
        } catch (ParseException e) {
            System.err.println("Program contains parse errors. Execution aborted.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (VMExecutionException e) {
            System.err.println("Runtime error: " + e.getMessage());
        }
    }

    public VM(String filePath) throws IOException, ParseException {
        this.lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.US_ASCII);
        parse();
    }

    private void parse() throws ParseException {
        List<String> errors = new ArrayList<>();

        int state = 0; // 0: none, 1: code, 2: data
        int address = 0;
        int lineNumber = 0;

        // First pass: collect labels and instruction addresses
        for (String rawLine : lines) {
            lineNumber++;
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.equals("code:")) {
                state = 1;
                continue;
            }

            if (line.equals("data:")) {
                state = 2;
                continue;
            }

            if (state == 1) {
                if (isLabel(line)) {
                    String labelName = extractLabelName(line);
                    if (labels.containsKey(labelName)) {
                        errors.add("Line " + lineNumber + ": duplicate label '" + labelName + "'");
                    } else {
                        labels.put(labelName, address);
                    }
                } else {
                    address++;
                }
            }
        }

        programEndAddress = address;

        // Second pass: parse code and data
        state = 0;
        address = 0;
        lineNumber = 0;

        for (String rawLine : lines) {
            lineNumber++;
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.equals("code:")) {
                state = 1;
                continue;
            }

            if (line.equals("data:")) {
                state = 2;
                continue;
            }

            if (state == 1) {
                if (isLabel(line)) {
                    continue;
                }

                try {
                    Instruction instruction = parseInstruction(line, lineNumber);
                    code.put(address++, instruction);
                } catch (ParseException e) {
                    errors.addAll(e.getErrors());
                }
            } else if (state == 2) {
                try {
                    parseDataLine(line, lineNumber);
                } catch (ParseException e) {
                    errors.addAll(e.getErrors());
                }
            } else {
                errors.add("Line " + lineNumber + ": content outside of 'code:' or 'data:' section");
            }
        }

        if (!code.containsKey(programEndAddress)) {
            code.put(programEndAddress, new HaltInstruction());
        }

        if (!errors.isEmpty()) {
            for (String error : errors) {
                System.err.println(error);
            }
            throw new ParseException(errors);
        }
    }

    private boolean isLabel(String line) {
        return line.matches("^_[a-zA-Z_][a-zA-Z0-9_]*:$");
    }

    private String extractLabelName(String line) {
        return line.substring(1, line.length() - 1);
    }

    private Instruction parseInstruction(String line, int lineNumber) throws ParseException {
        String[] parts = line.split(",");
        if (parts.length == 0) {
            throw new ParseException("Line " + lineNumber + ": empty instruction");
        }

        String instrName = parts[0].trim();
        InstructionKind kind = getInstructionKind(instrName);

        if (kind == null) {
            throw new ParseException("Line " + lineNumber + ": unknown instruction '" + instrName + "'");
        }

        String operand = parts.length > 1 ? parts[1].trim() : null;

        if (parts.length > 2) {
            throw new ParseException("Line " + lineNumber + ": too many operands for instruction '" + instrName + "'");
        }

        return switch (kind) {
            // Stack operations with typed operands
            case PUSHB -> new PushByteInstruction(parseByteOperand(operand, instrName, lineNumber));
            case PUSHC -> new PushCharInstruction(parseCharOperand(operand, instrName, lineNumber));
            case PUSHI -> new PushIntInstruction(parseIntOperand(operand, instrName, lineNumber));
            case PUSHD -> new PushDoubleInstruction(parseDoubleOperand(operand, instrName, lineNumber));

            // Stack operations without operands
            case POPB -> requireNoOperandAndReturn(new PopByteInstruction(), operand, instrName, lineNumber);
            case POPC -> requireNoOperandAndReturn(new PopCharInstruction(), operand, instrName, lineNumber);
            case POPI -> requireNoOperandAndReturn(new PopIntInstruction(), operand, instrName, lineNumber);
            case POPD -> requireNoOperandAndReturn(new PopDoubleInstruction(), operand, instrName, lineNumber);

            // Heap operations
            case HLOAD1 -> requireNoOperandAndReturn(new HLoad1Instruction(), operand, instrName, lineNumber);
            case HLOAD2 -> requireNoOperandAndReturn(new HLoad2Instruction(), operand, instrName, lineNumber);
            case HLOAD4 -> requireNoOperandAndReturn(new HLoad4Instruction(), operand, instrName, lineNumber);
            case HLOAD8 -> requireNoOperandAndReturn(new HLoad8Instruction(), operand, instrName, lineNumber);
            case HSTORE1 -> requireNoOperandAndReturn(new HStore1Instruction(), operand, instrName, lineNumber);
            case HSTORE2 -> requireNoOperandAndReturn(new HStore2Instruction(), operand, instrName, lineNumber);
            case HSTORE4 -> requireNoOperandAndReturn(new HStore4Instruction(), operand, instrName, lineNumber);
            case HSTORE8 -> requireNoOperandAndReturn(new HStore8Instruction(), operand, instrName, lineNumber);
            case NEW -> requireNoOperandAndReturn(new NewInstruction(), operand, instrName, lineNumber);

            // Arithmetic
            case IADD -> requireNoOperandAndReturn(new IAddInstruction(), operand, instrName, lineNumber);
            case ISUB -> requireNoOperandAndReturn(new ISubInstruction(), operand, instrName, lineNumber);
            case IMUL -> requireNoOperandAndReturn(new IMulInstruction(), operand, instrName, lineNumber);
            case IDIV -> requireNoOperandAndReturn(new IDivInstruction(), operand, instrName, lineNumber);
            case IMOD -> requireNoOperandAndReturn(new IModInstruction(), operand, instrName, lineNumber);
            case DADD -> requireNoOperandAndReturn(new DAddInstruction(), operand, instrName, lineNumber);
            case DSUB -> requireNoOperandAndReturn(new DSubInstruction(), operand, instrName, lineNumber);
            case DMUL -> requireNoOperandAndReturn(new DMulInstruction(), operand, instrName, lineNumber);
            case DDIV -> requireNoOperandAndReturn(new DDivInstruction(), operand, instrName, lineNumber);

            // Comparisons
            case ILT -> requireNoOperandAndReturn(new ILtInstruction(), operand, instrName, lineNumber);
            case ILE -> requireNoOperandAndReturn(new ILeInstruction(), operand, instrName, lineNumber);
            case IGT -> requireNoOperandAndReturn(new IGtInstruction(), operand, instrName, lineNumber);
            case IGE -> requireNoOperandAndReturn(new IGeInstruction(), operand, instrName, lineNumber);
            case IEQ -> requireNoOperandAndReturn(new IEqInstruction(), operand, instrName, lineNumber);
            case INE -> requireNoOperandAndReturn(new INeInstruction(), operand, instrName, lineNumber);
            case DLT -> requireNoOperandAndReturn(new DLtInstruction(), operand, instrName, lineNumber);
            case DLE -> requireNoOperandAndReturn(new DLeInstruction(), operand, instrName, lineNumber);
            case DGT -> requireNoOperandAndReturn(new DGtInstruction(), operand, instrName, lineNumber);
            case DGE -> requireNoOperandAndReturn(new DGeInstruction(), operand, instrName, lineNumber);
            case DEQ -> requireNoOperandAndReturn(new DEqInstruction(), operand, instrName, lineNumber);
            case DNE -> requireNoOperandAndReturn(new DNeInstruction(), operand, instrName, lineNumber);

            // Shifts
            case ISHL -> requireNoOperandAndReturn(new IShlInstruction(), operand, instrName, lineNumber);
            case ISHR -> requireNoOperandAndReturn(new IShrInstruction(), operand, instrName, lineNumber);

            // Bit operations
            case IAND -> requireNoOperandAndReturn(new IAndInstruction(), operand, instrName, lineNumber);
            case IOR -> requireNoOperandAndReturn(new IOrInstruction(), operand, instrName, lineNumber);
            case IXOR -> requireNoOperandAndReturn(new IXorInstruction(), operand, instrName, lineNumber);

            // Unary
            case INEG -> requireNoOperandAndReturn(new INegInstruction(), operand, instrName, lineNumber);
            case DNEG -> requireNoOperandAndReturn(new DNegInstruction(), operand, instrName, lineNumber);
            case IINV -> requireNoOperandAndReturn(new IInvInstruction(), operand, instrName, lineNumber);

            // Jump instructions
            case JUMP -> new JumpInstruction(parseLabelOperand(operand, instrName, lineNumber));
            case JUMPT -> new JumpTrueInstruction(parseLabelOperand(operand, instrName, lineNumber));
            case JUMPF -> new JumpFalseInstruction(parseLabelOperand(operand, instrName, lineNumber));

            // Halt and print
            case HALT -> requireNoOperandAndReturn(new HaltInstruction(), operand, instrName, lineNumber);
            case PRINTB -> requireNoOperandAndReturn(new PrintByteInstruction(), operand, instrName, lineNumber);
            case PRINTC -> requireNoOperandAndReturn(new PrintCharInstruction(), operand, instrName, lineNumber);
            case PRINTI -> requireNoOperandAndReturn(new PrintIntInstruction(), operand, instrName, lineNumber);
            case PRINTD -> requireNoOperandAndReturn(new PrintDoubleInstruction(), operand, instrName, lineNumber);
        };
    }

    private void parseDataLine(String line, int lineNumber) throws ParseException {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            throw new ParseException("Line " + lineNumber + ": invalid data declaration");
        }

        String name = parts[0];
        int size;

        try {
            size = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid data element size '" + parts[1] + "'");
        }

        if (size != 1 && size != 2 && size != 4 && size != 8) {
            throw new ParseException("Line " + lineNumber + ": unsupported data element size " + size);
        }

        String[] hexes = parts[2].split(",");
        List<Byte> byteList = new ArrayList<>();

        try {
            for (String hex : hexes) {
                long val = Long.parseLong(hex.trim(), 16);
                for (int i = 0; i < size; i++) {
                    byteList.add((byte) (val >> (8 * i)));
                }
            }
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid hex literal in data section");
        }

        byte[] dataBytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            dataBytes[i] = byteList.get(i);
        }

        dataMap.put(name, dataBytes);
    }

    private byte parseByteOperand(String operand, String instrName, int lineNumber) throws ParseException {
        if (operand == null) {
            throw new ParseException("Line " + lineNumber + ": missing operand for instruction '" + instrName + "'");
        }

        try {
            return Byte.parseByte(operand);
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid byte operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private char parseCharOperand(String operand, String instrName, int lineNumber) throws ParseException {
        if (operand == null) {
            throw new ParseException("Line " + lineNumber + ": missing operand for instruction '" + instrName + "'");
        }

        try {
            return (char) Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid char operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private int parseIntOperand(String operand, String instrName, int lineNumber) throws ParseException {
        if (operand == null) {
            throw new ParseException("Line " + lineNumber + ": missing operand for instruction '" + instrName + "'");
        }

        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid int operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private double parseDoubleOperand(String operand, String instrName, int lineNumber) throws ParseException {
        if (operand == null) {
            throw new ParseException("Line " + lineNumber + ": missing operand for instruction '" + instrName + "'");
        }

        try {
            return Double.parseDouble(operand);
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid double operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private String parseLabelOperand(String operand, String instrName, int lineNumber) throws ParseException {
        if (operand == null || operand.isBlank()) {
            throw new ParseException("Line " + lineNumber + ": missing label operand for instruction '" + instrName + "'");
        }
        return operand;
    }

    private Instruction requireNoOperandAndReturn(Instruction instruction, String operand, String instrName, int lineNumber)
            throws ParseException {
        if (operand != null && !operand.isBlank()) {
            throw new ParseException("Line " + lineNumber + ": instruction '" + instrName + "' does not accept an operand");
        }
        return instruction;
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

            // Halt and Print
            case "HALT" -> InstructionKind.HALT;
            case "PRINTB" -> InstructionKind.PRINTB;
            case "PRINTC" -> InstructionKind.PRINTC;
            case "PRINTI" -> InstructionKind.PRINTI;
            case "PRINTD" -> InstructionKind.PRINTD;

            default -> null;
        };
    }

    public void run() {
        while (!halted && code.containsKey(pc)) {
            Instruction instruction = code.get(pc);
            instruction.execute(this);
            pc++;
        }
    }

    private void halt() {
        halted = true;
        pc = programEndAddress;
    }

    private int resolveLabel(String labelName) {
        Integer target = labels.get(labelName);
        if (target == null) {
            throw new VMExecutionException("Label '" + labelName + "' not found");
        }
        return target;
    }

    // Stack helper methods
    private void pushByte(byte val) {
        stack[sp++] = val;
    }

    private byte popByte() {
        if (sp < 1) {
            throw new VMExecutionException("Stack underflow on byte pop");
        }
        return stack[--sp];
    }

    private void pushChar(char val) {
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
    }

    private char popChar() {
        if (sp < 2) {
            throw new VMExecutionException("Stack underflow on char pop");
        }
        byte high = stack[--sp];
        byte low = stack[--sp];
        return (char) (((high & 0xFF) << 8) | (low & 0xFF));
    }

    private void pushInt(int val) {
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
        stack[sp++] = (byte) ((val >> 16) & 0xFF);
        stack[sp++] = (byte) ((val >> 24) & 0xFF);
    }

    private int popInt() {
        if (sp < 4) {
            throw new VMExecutionException("Stack underflow on int pop");
        }
        byte b3 = stack[--sp];
        byte b2 = stack[--sp];
        byte b1 = stack[--sp];
        byte b0 = stack[--sp];
        return ((b3 & 0xFF) << 24)
                | ((b2 & 0xFF) << 16)
                | ((b1 & 0xFF) << 8)
                | (b0 & 0xFF);
    }

    private void pushDouble(double val) {
        long bits = Double.doubleToLongBits(val);
        for (int i = 0; i < 8; i++) {
            stack[sp++] = (byte) (bits & 0xFF);
            bits >>= 8;
        }
    }

    private double popDouble() {
        if (sp < 8) {
            throw new VMExecutionException("Stack underflow on double pop");
        }
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits |= ((long) (stack[--sp] & 0xFF)) << (8 * i);
        }
        return Double.longBitsToDouble(bits);
    }

    private byte[] getHeapObject(int index) {
        if (index < 0 || index >= heap.size()) {
            throw new VMExecutionException("Invalid heap reference: " + index);
        }
        return heap.get(index);
    }

    private void checkHeapAccess(byte[] obj, int offset, int size) {
        if (offset < 0 || offset + size > obj.length) {
            throw new VMExecutionException("Heap access out of bounds (offset=" + offset + ", size=" + size + ")");
        }
    }

    // ===== Instruction hierarchy =====

    private static abstract class Instruction {
        public abstract void execute(VM vm);
    }

    // ===== Stack operations =====

    private static final class PushByteInstruction extends Instruction {
        private final byte value;

        public PushByteInstruction(byte value) {
            this.value = value;
        }

        @Override
        public void execute(VM vm) {
            vm.pushByte(value);
        }
    }

    private static final class PushCharInstruction extends Instruction {
        private final char value;

        public PushCharInstruction(char value) {
            this.value = value;
        }

        @Override
        public void execute(VM vm) {
            vm.pushChar(value);
        }
    }

    private static final class PushIntInstruction extends Instruction {
        private final int value;

        public PushIntInstruction(int value) {
            this.value = value;
        }

        @Override
        public void execute(VM vm) {
            vm.pushInt(value);
        }
    }

    private static final class PushDoubleInstruction extends Instruction {
        private final double value;

        public PushDoubleInstruction(double value) {
            this.value = value;
        }

        @Override
        public void execute(VM vm) {
            vm.pushDouble(value);
        }
    }

    private static final class PopByteInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.popByte();
        }
    }

    private static final class PopCharInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.popChar();
        }
    }

    private static final class PopIntInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.popInt();
        }
    }

    private static final class PopDoubleInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.popDouble();
        }
    }

    // ===== Heap operations =====

    private static final class HLoad1Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 1);
            vm.pushByte(obj[offset]);
        }
    }

    private static final class HLoad2Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 2);

            byte low = obj[offset];
            byte high = obj[offset + 1];
            char value = (char) (((high & 0xFF) << 8) | (low & 0xFF));
            vm.pushChar(value);
        }
    }

    private static final class HLoad4Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 4);

            int value = 0;
            for (int i = 0; i < 4; i++) {
                value |= (obj[offset + i] & 0xFF) << (8 * i);
            }
            vm.pushInt(value);
        }
    }

    private static final class HLoad8Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 8);

            long bits = 0;
            for (int i = 0; i < 8; i++) {
                bits |= ((long) (obj[offset + i] & 0xFF)) << (8 * i);
            }
            vm.pushDouble(Double.longBitsToDouble(bits));
        }
    }

    private static final class HStore1Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 1);
            obj[offset] = value;
        }
    }

    private static final class HStore2Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            char value = vm.popChar();
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 2);

            obj[offset] = (byte) (value & 0xFF);
            obj[offset + 1] = (byte) ((value >> 8) & 0xFF);
        }
    }

    private static final class HStore4Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int value = vm.popInt();
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 4);

            for (int i = 0; i < 4; i++) {
                obj[offset + i] = (byte) (value & 0xFF);
                value >>= 8;
            }
        }
    }

    private static final class HStore8Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double dValue = vm.popDouble();
            long value = Double.doubleToLongBits(dValue);
            int offset = vm.popInt();
            int base = vm.popInt();
            byte[] obj = vm.getHeapObject(base);
            vm.checkHeapAccess(obj, offset, 8);

            for (int i = 0; i < 8; i++) {
                obj[offset + i] = (byte) (value & 0xFF);
                value >>= 8;
            }
        }
    }

    private static final class NewInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int size = vm.popInt();
            if (size < 0) {
                throw new VMExecutionException("Negative heap allocation size: " + size);
            }
            vm.heap.add(new byte[size]);
            vm.pushInt(vm.heap.size() - 1);
        }
    }

    // ===== Arithmetic =====

    private static final class IAddInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.popInt() + vm.popInt());
        }
    }

    private static final class ISubInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushInt(a - b);
        }
    }

    private static final class IMulInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.popInt() * vm.popInt());
        }
    }

    private static final class IDivInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            if (b == 0) {
                throw new VMExecutionException("Division by zero");
            }
            vm.pushInt(a / b);
        }
    }

    private static final class IModInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            if (b == 0) {
                throw new VMExecutionException("Modulo by zero");
            }
            vm.pushInt(a % b);
        }
    }

    private static final class DAddInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushDouble(vm.popDouble() + vm.popDouble());
        }
    }

    private static final class DSubInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            vm.pushDouble(a - b);
        }
    }

    private static final class DMulInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushDouble(vm.popDouble() * vm.popDouble());
        }
    }

    private static final class DDivInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            if (b == 0.0) {
                throw new VMExecutionException("Division by zero");
            }
            vm.pushDouble(a / b);
        }
    }

    // ===== Comparisons =====

    private static final class ILtInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushByte((byte) (a < b ? 1 : 0));
        }
    }

    private static final class ILeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushByte((byte) (a <= b ? 1 : 0));
        }
    }

    private static final class IGtInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushByte((byte) (a > b ? 1 : 0));
        }
    }

    private static final class IGeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushByte((byte) (a >= b ? 1 : 0));
        }
    }

    private static final class IEqInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushByte((byte) (vm.popInt() == vm.popInt() ? 1 : 0));
        }
    }

    private static final class INeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushByte((byte) (vm.popInt() != vm.popInt() ? 1 : 0));
        }
    }

    private static final class DLtInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            vm.pushByte((byte) (a < b ? 1 : 0));
        }
    }

    private static final class DLeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            vm.pushByte((byte) (a <= b ? 1 : 0));
        }
    }

    private static final class DGtInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            vm.pushByte((byte) (a > b ? 1 : 0));
        }
    }

    private static final class DGeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double b = vm.popDouble();
            double a = vm.popDouble();
            vm.pushByte((byte) (a >= b ? 1 : 0));
        }
    }

    private static final class DEqInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushByte((byte) (vm.popDouble() == vm.popDouble() ? 1 : 0));
        }
    }

    private static final class DNeInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushByte((byte) (vm.popDouble() != vm.popDouble() ? 1 : 0));
        }
    }

    // ===== Shifts =====

    private static final class IShlInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushInt(a << b);
        }
    }

    private static final class IShrInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int b = vm.popInt();
            int a = vm.popInt();
            vm.pushInt(a >> b);
        }
    }

    // ===== Bit operations =====

    private static final class IAndInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.popInt() & vm.popInt());
        }
    }

    private static final class IOrInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.popInt() | vm.popInt());
        }
    }

    private static final class IXorInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.popInt() ^ vm.popInt());
        }
    }

    // ===== Unary =====

    private static final class INegInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(-vm.popInt());
        }
    }

    private static final class DNegInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushDouble(-vm.popDouble());
        }
    }

    private static final class IInvInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.pushInt(~vm.popInt());
        }
    }

    // ===== Jumps =====

    private static final class JumpInstruction extends Instruction {
        private final String labelName;

        public JumpInstruction(String labelName) {
            this.labelName = labelName;
        }

        @Override
        public void execute(VM vm) {
            int target = vm.resolveLabel(labelName);
            vm.pc = target - 1;
        }
    }

    private static final class JumpTrueInstruction extends Instruction {
        private final String labelName;

        public JumpTrueInstruction(String labelName) {
            this.labelName = labelName;
        }

        @Override
        public void execute(VM vm) {
            byte condition = vm.popByte();
            if (condition != 0) {
                int target = vm.resolveLabel(labelName);
                vm.pc = target - 1;
            }
        }
    }

    private static final class JumpFalseInstruction extends Instruction {
        private final String labelName;

        public JumpFalseInstruction(String labelName) {
            this.labelName = labelName;
        }

        @Override
        public void execute(VM vm) {
            byte condition = vm.popByte();
            if (condition == 0) {
                int target = vm.resolveLabel(labelName);
                vm.pc = target - 1;
            }
        }
    }

    // ===== Halt / Print =====

    private static final class HaltInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.halt();
        }
    }

    private static final class PrintByteInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            System.out.print(vm.popByte());
        }
    }

    private static final class PrintCharInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            System.out.print(vm.popChar());
        }
    }

    private static final class PrintIntInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            System.out.print(vm.popInt());
        }
    }

    private static final class PrintDoubleInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            System.out.print(vm.popDouble());
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

        // Halt and Print
        HALT, PRINTB, PRINTC, PRINTI, PRINTD
    }

    private static final class ParseException extends Exception {
        private final List<String> errors;

        public ParseException(String error) {
            super(error);
            this.errors = List.of(error);
        }

        public ParseException(List<String> errors) {
            super(String.join(System.lineSeparator(), errors));
            this.errors = List.copyOf(errors);
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    private static final class VMExecutionException extends RuntimeException {
        public VMExecutionException(String message) {
            super(message);
        }
    }
}