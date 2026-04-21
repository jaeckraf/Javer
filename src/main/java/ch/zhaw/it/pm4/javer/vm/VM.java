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
            String trimmed = line.trim();
            if (trimmed.contains("//")) {
                trimmed = trimmed.substring(0, trimmed.indexOf("//")).trim();
            }
            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.equalsIgnoreCase("data:")) {
                isDataSection = true;
                continue;
            } else if (trimmed.equalsIgnoreCase("code:")) {
                isDataSection = false;
                continue;
            }

            if (isDataSection) {
                String[] parts = trimmed.split("\\s+");
                for (String val : parts) {
                    try {
                        int numValue;
                        if (val.matches("-?\\d+")) {
                            numValue = Integer.parseInt(val);
                        } else {
                            numValue = val.charAt(0);
                        }
                        
                        int[] element = new int[1];
                        element[0] = numValue;
                        heap.add(element);
                    } catch (Exception e) {
                        System.out.println("Invalid data entry: " + val);
                    }
                }
            } else {
                processedLines.add(trimmed);
            }
        }

        this.segment = processedLines.toArray(new String[0]);
    }

    public String run() {
        boolean halted = false;

        while (!halted && pc < segment.length) {
            String currentInstruction = segment[pc];
            String[] parts = currentInstruction.split("\\s+");
            String opcode = parts[0].toUpperCase();

            switch (opcode) {
                case "PUSHCONST":
                    stack.push(parseValue(parts[1]));
                    pc++;
                    break;
                case "PUSH":
                    stack.push(parseValue(parts[1]));
                    pc++;
                    break;
                case "POP":
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                    pc++;
                    break;
                case "PRINT":
                    if (!stack.isEmpty()) {
                        System.out.println(stack.pop());
                    }
                    pc++;
                    break;
                case "PRINTC":
                    if (!stack.isEmpty()) {
                        Object val = stack.pop();
                        if (val instanceof Number) {
                            System.out.print((char) ((Number) val).intValue());
                        }
                    }
                    pc++;
                    break;
                case "ADD":
                    binaryOp(Integer::sum);
                    pc++;
                    break;
                case "SUB":
                    binaryOp((a, b) -> a - b);
                    pc++;
                    break;
                case "MUL":
                    binaryOp((a, b) -> a * b);
                    pc++;
                    break;
                case "DIV":
                    binaryOp((a, b) -> a / b);
                    pc++;
                    break;
                case "MOD":
                    binaryOp((a, b) -> a % b);
                    pc++;
                    break;
                case "SHIFTR":
                    binaryOp((a, b) -> a >> b);
                    pc++;
                    break;
                case "SHIFTL":
                    binaryOp((a, b) -> a << b);
                    pc++;
                    break;
                case "AND":
                    binaryOp((a, b) -> a & b);
                    pc++;
                    break;
                case "OR":
                    binaryOp((a, b) -> a | b);
                    pc++;
                    break;
                case "XOR":
                    binaryOp((a, b) -> a ^ b);
                    pc++;
                    break;
                case "LT":
                    binaryOp((a, b) -> a < b ? 1 : 0);
                    pc++;
                    break;
                case "LE":
                    binaryOp((a, b) -> a <= b ? 1 : 0);
                    pc++;
                    break;
                case "GT":
                    binaryOp((a, b) -> a > b ? 1 : 0);
                    pc++;
                    break;
                case "GE":
                    binaryOp((a, b) -> a >= b ? 1 : 0);
                    pc++;
                    break;
                case "COMPARE":
                    binaryOp((a, b) -> a.equals(b) ? 1 : 0);
                    pc++;
                    break;
                case "ADDF":
                    binaryOpF(Float::sum);
                    pc++;
                    break;
                case "SUBF":
                    binaryOpF((a, b) -> a - b);
                    pc++;
                    break;
                case "MULF":
                    binaryOpF((a, b) -> a * b);
                    pc++;
                    break;
                case "DIVF":
                    binaryOpF((a, b) -> a / b);
                    pc++;
                    break;
                case "BITWISEINVERT":
                    unaryOp(a -> ~a);
                    pc++;
                    break;
                case "NEGATE":
                    unaryOp(a -> -a);
                    pc++;
                    break;
                case "NEGATEF":
                    unaryOpF(a -> -a);
                    pc++;
                    break;
                case "JMP":
                    pc = getJumpTarget(parts[1]);
                    if (pc == -1) halted = true;
                    break;
                case "JT":
                    if (isTopTrue()) {
                        pc = getJumpTarget(parts[1]);
                        if (pc == -1) halted = true;
                    } else {
                        pc++;
                    }
                    break;
                case "JF":
                    if (!isTopTrue()) {
                        pc = getJumpTarget(parts[1]);
                        if (pc == -1) halted = true;
                    } else {
                        pc++;
                    }
                    break;
                case "CALL":
                    int targetPc = getJumpTarget(parts[1]);
                    if (targetPc != -1) {
                        stack.push(pc + 1);
                        pc = targetPc;
                    } else {
                        halted = true;
                    }
                    break;
                case "RET":
                    if (!stack.isEmpty()) {
                        Object returnAddress = stack.pop();
                        if (returnAddress instanceof Integer) {
                            pc = (Integer) returnAddress;
                        } else {
                            System.out.println("outor: Return address on stack is not an Integer.");
                            halted = true;
                        }
                    } else {
                        halted = true;
                    }
                    break;
                case "HALT":
                    halted = true;
                    break;
                case "NEW":
                    int size = ((Number) stack.pop()).intValue();
                    heap.add(new int[size]);
                    stack.push(heap.size() - 1);
                    pc++;
                    break;
                case "HLOAD":
                    int offset = Integer.parseInt(parts[2]);
                    int pointerIndex = ((Number) stack.pop()).intValue();
                    int[] hloadArray = heap.get(pointerIndex);
                    stack.push(hloadArray[offset]);
                    pc++;
                    break;
                case "HSTORE":
                    Object value = stack.pop();
                    int hstoreOffset = Integer.parseInt(parts[2]);
                    int hstorePointerIndex = ((Number) stack.pop()).intValue();
                    int[] hstoreArray = heap.get(hstorePointerIndex);
                    hstoreArray[hstoreOffset] = ((Number) value).intValue();
                    pc++;
                    break;
                default:
                    System.out.println("Unknown instruction at line " + (pc + 1) + ": " + currentInstruction);
                    pc++;
                    break;
            }
        }
        return "Execution finished.";
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
