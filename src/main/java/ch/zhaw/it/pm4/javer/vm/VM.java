package ch.zhaw.it.pm4.javer.vm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VM {

    // Single segment for code and data, indexed by PC
    private String[] segment;
    private int pc = 0;

    // A single stack for all data (use Object, since we push numbers and strings)
    private Deque<Object> stack = new ArrayDeque<>();

    // Heap as int array
    private int[] heap = new int[1024];

    // Map for resolving jump labels (e.g. "#200" -> Index 8)
    private Map<String, Integer> labels = new HashMap<>();

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

        for (String line : rawLines) {
            String trimmed = line.trim();
            // Ignore certain keywords or empty lines
            if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("code:") || trimmed.equalsIgnoreCase("data:")) {
                continue;
            }

            // Remove comments (everything after //)
            if (trimmed.contains("//")) {
                trimmed = trimmed.substring(0, trimmed.indexOf("//")).trim();
            }

            if (trimmed.isEmpty()) {
                continue;
            }

            // Register labels (starting with #)
            if (trimmed.startsWith("#")) {
                String[] parts = trimmed.split("\\s+");
                String labelName = parts[0];
                labels.put(labelName, processedLines.size());
            }

            processedLines.add(trimmed);
        }

        this.segment = processedLines.toArray(new String[0]);
    }

    public String run() {
        boolean halted = false;

        while (!halted && pc < segment.length) {
            String currentInstruction = segment[pc];

            // On-the-fly decoding of the instruction
            String[] parts = currentInstruction.split("\\s+");
            String opcode = parts[0].toUpperCase();

            switch (opcode) {
                case "PUSH":
                    if (parts.length > 2 && parts[1].equalsIgnoreCase("CONST")) {
                        // e.g. PUSH CONST 100
                        stack.push(parseValue(parts[2]));
                    } else if (parts.length > 2 && parts[1].equalsIgnoreCase("ARGS")) {
                        stack.push(parseValue(parts[2]));
                    } else if (parts.length > 1) {
                        stack.push(parseValue(parts[1]));
                    } else {
                        System.err.println("Invalid PUSH on PC " + pc);
                        halted = true;
                    }
                    pc++;
                    break;
                case "POP":
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                    pc++;
                    break;
                case "ADD":
                    if (stack.size() >= 2) {
                        Number b = (Number) stack.pop();
                        Number a = (Number) stack.pop();
                        stack.push(a.intValue() + b.intValue());
                    }
                    pc++;
                    break;
                case "ADDF":
                    if (stack.size() >= 2) {
                        Number bF = (Number) stack.pop();
                        Number aF = (Number) stack.pop();
                        stack.push(aF.floatValue() + bF.floatValue());
                    }
                    pc++;
                    break;
                case "JMP":
                    // Formatting of JMP 200 or JMP #200
                    String target = parts[1].startsWith("#") ? parts[1] : "#" + parts[1];
                    if (labels.containsKey(target)) {
                        pc = labels.get(target);
                    } else {
                        System.err.println("Unknown label: " + target);
                        halted = true;
                    }
                    break;
                case "HALT":
                    halted = true;
                    break;
                default:
                    if (opcode.startsWith("#")) {
                        // It is a label / data entry, just ignore and continue
                        pc++;
                    } else {
                        System.out.println("Unknown instruction on PC " + pc + ": " + opcode);
                        pc++;
                    }
                    break;
            }
        }
        return "Execution finished.";
    }

    private Object parseValue(String val) {
        try {
            if (val.contains(".")) {
                return Float.parseFloat(val);
            } else {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException e) {
            return val; // Fallback as String
        }
    }

}
