package ch.zhaw.it.pm4.javer.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VM {
    private String[] segment;
    private Object[] dataSegment;
    private int pc = 0;
    private final Deque<Object> stack = new ArrayDeque<>();
    private final List<int[]> heap = new ArrayList<>();

    /**
     * Constructs a new VM by loading the compiled program from the given file.
     * The file is expected to contain an optional 'data:' section for static data,
     * and a 'code:' section for instructions.
     *
     * @param filePath the path to the compiled code file to execute.
     */
    public VM(String filePath) {
        try {
            List<String> rawLines = Files.readAllLines(Paths.get(filePath));
            loadSegment(rawLines);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    public static void main(String[] args) {
        System.setProperty("MODULE", "vm");
        if (args.length < 1) {
            System.err.println("Usage: java VM <bytecode-file>");
            System.exit(1);
        }
        String bytecodeFile = args[0];
        BytecodeLoader bytecodeLoader = new BytecodeLoader(bytecodeFile);
        VM vm = new VM(bytecodeLoader);
        String result = vm.run();
        System.out.println("test");
        System.err.println("error test");
    }

    /**
     * Starts execution of the instructions loaded into the VM's code segment.
     * Execution continues until a HALT instruction is reached, or the program counter
     * runs past the end of the segment.
     *
     * @return A status message indicating execution has finished.
     */
    public String run() {
        boolean halted = false;

        while (!halted && pc < segment.length) {
            String currentInstruction = segment[pc];
            String[] parts = currentInstruction.split("\\s+");
            try {
                halted = executeInstruction(parts, currentInstruction);
            } catch (VMRuntimeException e) {
                System.out.println(e.getMessage());
                halted = true;
            }
        }
        return "Execution finished.";
    }

    private void loadSegment(List<String> rawLines) {
        List<String> processedLines = new ArrayList<>();
        List<Object> dataLines = new ArrayList<>(); // Temporary list for static data
        boolean isDataSection = false;

        for (String line : rawLines) {
            String trimmed = stripComments(line);
            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.equalsIgnoreCase("data:")) {
                isDataSection = true;
            } else if (trimmed.equalsIgnoreCase("code:")) {
                isDataSection = false;
            } else if (isDataSection) {
                parseDataEntry(trimmed, dataLines); // Pass the new list instead of using heap
            } else {
                processedLines.add(trimmed);
            }
        }

        this.segment = processedLines.toArray(new String[0]);
        this.dataSegment = dataLines.toArray(); // Initialize the separate data section
    }

    private String stripComments(String line) {
        String trimmed = line.trim();
        int commentIdx = trimmed.indexOf("//");
        return commentIdx != -1 ? trimmed.substring(0, commentIdx).trim() : trimmed;
    }

    private void parseDataEntry(String entry, List<Object> dataLines) {
        String[] parts = entry.split("\\s+");
        for (String val : parts) {
            dataLines.add(parseValue(val));
        }
    }

    private boolean executeInstruction(String[] parts, String currentInstruction) {
        String opcode = parts[0].toUpperCase();

        switch (opcode) {
            // Stack Operations
            case "PUSH" -> {
                stack.push(parseValue(parts[1]));
                pc++;
            }
            case "POP" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on POP");
                stack.pop();
                pc++;
            }

            // Static Data Operation
            case "LOAD" -> {
                int index = Integer.parseInt(parts[1]);
                if (index < 0 || index >= dataSegment.length) {
                    throw new VMRuntimeException(pc, "Data segment access out of bounds: " + index);
                }
                stack.push(dataSegment[index]);
                pc++;
            }

            // Output Operations
            case "PRINT" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on PRINT");
                System.out.println(stack.pop());
                pc++;
            }
            case "PRINTC" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on PRINTC");
                Object val = stack.pop();
                if (val instanceof Number num) {
                    System.out.print((char) num.intValue());
                } else {
                    throw new VMRuntimeException(pc, "Type mismatch: PRINTC expects a Number");
                }
                pc++;
            }

            // Integer Math & Bitwise Operations
            case "ADD" -> { binaryOp(Integer::sum); pc++; }
            case "SUB" -> { binaryOp((a, b) -> a - b); pc++; }
            case "MUL" -> { binaryOp((a, b) -> a * b); pc++; }
            case "DIV" -> {
                binaryOp((a, b) -> {
                    if (b == 0) throw new VMRuntimeException(pc, "Division by zero");
                    return a / b;
                });
                pc++;
            }
            case "MOD" -> {
                binaryOp((a, b) -> {
                    if (b == 0) throw new VMRuntimeException(pc, "Modulo by zero");
                    return a % b;
                });
                pc++;
            }
            case "SHIFTR" -> { binaryOp((a, b) -> a >> b); pc++; }
            case "SHIFTL" -> { binaryOp((a, b) -> a << b); pc++; }
            case "AND" -> { binaryOp((a, b) -> a & b); pc++; }
            case "OR" -> { binaryOp((a, b) -> a | b); pc++; }
            case "XOR" -> { binaryOp((a, b) -> a ^ b); pc++; }
            case "BITWISEINVERT" -> { unaryOp(a -> ~a); pc++; }
            case "NEGATE" -> { unaryOp(a -> -a); pc++; }

            // Relational & Equality Operations
            case "LT" -> { binaryOp((a, b) -> a < b ? 1 : 0); pc++; }
            case "LE" -> { binaryOp((a, b) -> a <= b ? 1 : 0); pc++; }
            case "GT" -> { binaryOp((a, b) -> a > b ? 1 : 0); pc++; }
            case "GE" -> { binaryOp((a, b) -> a >= b ? 1 : 0); pc++; }
            case "COMPARE" -> { binaryOp((a, b) -> a.equals(b) ? 1 : 0); pc++; }

            // Floating Point Math
            case "ADDF" -> { binaryOpF(Float::sum); pc++; }
            case "SUBF" -> { binaryOpF((a, b) -> a - b); pc++; }
            case "MULF" -> { binaryOpF((a, b) -> a * b); pc++; }
            case "DIVF" -> {
                binaryOpF((a, b) -> {
                    if (b == 0.0f) throw new VMRuntimeException(pc, "Division by zero");
                    return a / b;
                });
                pc++;
            }
            case "NEGATEF" -> { unaryOpF(a -> -a); pc++; }

            // Branching & Control Flow
            case "JMP" -> {
                pc = getJumpTarget(parts[1]);
            }
            case "JT" -> {
                if (isTopTrue()) {
                    pc = getJumpTarget(parts[1]);
                } else pc++;
            }
            case "JF" -> {
                if (!isTopTrue()) {
                    pc = getJumpTarget(parts[1]);
                } else pc++;
            }
            case "RET" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on RET: Missing return address");
                Object retAddr = stack.pop();
                if (retAddr instanceof Integer returnAddress) {
                    pc = returnAddress;
                } else {
                    throw new VMRuntimeException(pc, "Type mismatch: Return address on stack is not an Integer");
                }
            }
            case "HALT" -> {
                return true;
            }

            // Heap & Memory Operations
            case "NEW" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on NEW");
                Object val = stack.pop();
                if (val instanceof Number size) {
                    heap.add(new int[size.intValue()]);
                    stack.push(heap.size() - 1);
                    pc++;
                } else {
                    throw new VMRuntimeException(pc, "Type mismatch: NEW expects a Number for size");
                }
            }
            case "HLOAD" -> {
                if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow on HLOAD");
                int offset = Integer.parseInt(parts[2]);
                Object ptr = stack.pop();
                if (ptr instanceof Number pointerIndex) {
                    stack.push(heap.get(pointerIndex.intValue())[offset]);
                    pc++;
                } else {
                    throw new VMRuntimeException(pc, "Type mismatch: HLOAD expects a Number for pointer index");
                }
            }
            case "HSTORE" -> {
                if (stack.size() < 2) throw new VMRuntimeException(pc, "Stack underflow on HSTORE");
                Object value = stack.pop();
                Object ptr = stack.pop();
                int offset = Integer.parseInt(parts[2]);
                if (ptr instanceof Number pointerIndex && value instanceof Number valNum) {
                    heap.get(pointerIndex.intValue())[offset] = valNum.intValue();
                    pc++;
                } else {
                    throw new VMRuntimeException(pc, "Type mismatch: HSTORE expects Numbers for pointer and value");
                }
            }

            // Fallback
            default -> {
                System.out.println("Unknown instruction at line " + (pc + 1) + ": " + currentInstruction);
                pc++;
            }
        }
        return false;
    }

    private void binaryOp(BiFunction<Integer, Integer, Integer> op) {
        if (stack.size() < 2) {
            throw new VMRuntimeException(pc, "Stack underflow: Not enough operands for binary operation.");
        }
        Number b = (Number) stack.pop();
        Number a = (Number) stack.pop();
        stack.push(op.apply(a.intValue(), b.intValue()));
    }

    private void unaryOp(Function<Integer, Integer> op) {
        if (stack.isEmpty()) {
            throw new VMRuntimeException(pc, "Stack underflow: Missing operand for unary operation.");
        }
        Number a = (Number) stack.pop();
        stack.push(op.apply(a.intValue()));
    }

    private void binaryOpF(BiFunction<Float, Float, Float> op) {
        if (stack.size() < 2) {
            throw new VMRuntimeException(pc, "Stack underflow: Not enough operands for float binary operation.");
        }
        Number b = (Number) stack.pop();
        Number a = (Number) stack.pop();
        stack.push(op.apply(a.floatValue(), b.floatValue()));
    }

    private void unaryOpF(Function<Float, Float> op) {
        if (stack.isEmpty()) {
            throw new VMRuntimeException(pc, "Stack underflow: Missing operand for float unary operation.");
        }
        Number a = (Number) stack.pop();
        stack.push(op.apply(a.floatValue()));
    }

    private boolean isTopTrue() {
        if (stack.isEmpty()) throw new VMRuntimeException(pc, "Stack underflow during condition check");
        Object top = stack.pop();
        if (top instanceof Number) {
            return ((Number) top).intValue() != 0;
        }
        return false; // Non-numeric values are considered false
    }

    private int getJumpTarget(String target) {
        try {
            int line = Integer.parseInt(target);
            int targetPc = line - 1;

            if (targetPc >= 0 && targetPc < segment.length) {
                return targetPc;
            } else {
                throw new VMRuntimeException(pc, "Jump target out of bounds: line " + line);
            }

        } catch (NumberFormatException e) {
            throw new VMRuntimeException(pc, "Invalid jump target format: '" + target + "' is not a number.");
        }
    }

    private Object parseValue(String val) {
        if (val.startsWith("\"") && val.endsWith("\"")) {
            return val.substring(1, val.length() - 1);
        }
        try {
            if (val.contains(".")) {
                return Float.parseFloat(val);
            } else {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException e) {
            return val;
        }
    }
}
