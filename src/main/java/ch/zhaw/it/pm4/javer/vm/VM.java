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
    private int pc = 0;
    private final Deque<Object> stack = new ArrayDeque<>();
    private final List<int[]> heap = new java.util.LinkedList<>();

    public VM(String filePath) {
        try {
            List<String> rawLines = Files.readAllLines(Paths.get(filePath));
            loadSegment(rawLines);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    private void loadSegment(List<String> rawLines) {
        List<String> processedLines = new ArrayList<>();
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
                parseDataEntry(trimmed);
            } else {
                processedLines.add(trimmed);
            }
        }

        this.segment = processedLines.toArray(new String[0]);
    }

    private String stripComments(String line) {
        String trimmed = line.trim();
        int commentIdx = trimmed.indexOf("//");
        return commentIdx != -1 ? trimmed.substring(0, commentIdx).trim() : trimmed;
    }

    private void parseDataEntry(String entry) {
        String[] parts = entry.split("\\s+");
        for (String val : parts) {
            try {
                int numValue = val.matches("-?\\d+") ? Integer.parseInt(val) : val.charAt(0);
                heap.add(new int[]{ numValue });
            } catch (Exception e) {
                System.out.println("Invalid data entry: " + val);
            }
        }
    }

    public String run() {
        boolean halted = false;

        while (!halted && pc < segment.length) {
            String currentInstruction = segment[pc];
            String[] parts = currentInstruction.split("\\s+");
            halted = executeInstruction(parts, currentInstruction);
        }
        return "Execution finished.";
    }

    private boolean executeInstruction(String[] parts, String currentInstruction) {
        String opcode = parts[0].toUpperCase();

        switch (opcode) {
            // Stack Operations
            case "PUSHCONST", "PUSH" -> {
                stack.push(parseValue(parts[1]));
                pc++;
            }
            case "POP" -> {
                if (!stack.isEmpty()) stack.pop();
                pc++;
            }
            
            // Output Operations
            case "PRINT" -> {
                if (!stack.isEmpty()) System.out.println(stack.pop());
                pc++;
            }
            case "PRINTC" -> {
                if (!stack.isEmpty() && stack.pop() instanceof Number val) {
                    System.out.print((char) val.intValue());
                }
                pc++;
            }
            
            // Integer Math & Bitwise Operations
            case "ADD" -> { binaryOp(Integer::sum); pc++; }
            case "SUB" -> { binaryOp((a, b) -> a - b); pc++; }
            case "MUL" -> { binaryOp((a, b) -> a * b); pc++; }
            case "DIV" -> { binaryOp((a, b) -> a / b); pc++; }
            case "MOD" -> { binaryOp((a, b) -> a % b); pc++; }
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
            case "DIVF" -> { binaryOpF((a, b) -> a / b); pc++; }
            case "NEGATEF" -> { unaryOpF(a -> -a); pc++; }
            
            // Branching & Control Flow
            case "JMP" -> {
                pc = getJumpTarget(parts[1]);
                if (pc == -1) return true;
            }
            case "JT" -> {
                if (isTopTrue()) {
                    pc = getJumpTarget(parts[1]);
                    if (pc == -1) return true;
                } else pc++;
            }
            case "JF" -> {
                if (!isTopTrue()) {
                    pc = getJumpTarget(parts[1]);
                    if (pc == -1) return true;
                } else pc++;
            }
            case "CALL" -> {
                int targetPc = getJumpTarget(parts[1]);
                if (targetPc != -1) {
                    stack.push(pc + 1);
                    pc = targetPc;
                } else return true;
            }
            case "RET" -> {
                if (!stack.isEmpty() && stack.pop() instanceof Integer returnAddress) {
                    pc = returnAddress;
                } else {
                    System.out.println("outor: Return address on stack is missing or not an Integer.");
                    return true;
                }
            }
            case "HALT" -> {
                return true;
            }
            
            // Heap & Memory Operations
            case "NEW" -> {
                if (!stack.isEmpty() && stack.pop() instanceof Number size) {
                    heap.add(new int[size.intValue()]);
                    stack.push(heap.size() - 1);
                    pc++;
                }
            }
            case "HLOAD" -> {
                int offset = Integer.parseInt(parts[2]);
                if (!stack.isEmpty() && stack.pop() instanceof Number pointerIndex) {
                    stack.push(heap.get(pointerIndex.intValue())[offset]);
                    pc++;
                }
            }
            case "HSTORE" -> {
                Object value = stack.pop();
                int offset = Integer.parseInt(parts[2]);
                if (!stack.isEmpty() && stack.pop() instanceof Number pointerIndex && value instanceof Number valNum) {
                    heap.get(pointerIndex.intValue())[offset] = valNum.intValue();
                    pc++;
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
        if (stack.size() >= 2) {
            Number b = (Number) stack.pop();
            Number a = (Number) stack.pop();
            stack.push(op.apply(a.intValue(), b.intValue()));
        }
    }

    private void unaryOp(Function<Integer, Integer> op) {
        if (!stack.isEmpty()) {
            Number a = (Number) stack.pop();
            stack.push(op.apply(a.intValue()));
        }
    }

    private void binaryOpF(BiFunction<Float, Float, Float> op) {
        if (stack.size() >= 2) {
            Number b = (Number) stack.pop();
            Number a = (Number) stack.pop();
            stack.push(op.apply(a.floatValue(), b.floatValue()));
        }
    }

    private void unaryOpF(Function<Float, Float> op) {
        if (!stack.isEmpty()) {
            Number a = (Number) stack.pop();
            stack.push(op.apply(a.floatValue()));
        }
    }

    private boolean isTopTrue() {
        if (stack.isEmpty()) return false;
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
            }
        } catch (NumberFormatException e) {
            // Handled below
        }
        System.out.println("Invalid jump target: '" + target + "' is not a valid line number.");
        return -1;
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
