package ch.zhaw.it.pm4.javer.vm;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stack-based virtual machine that parses and executes Javer bytecode.
 */
public class VM {

    private static final int DEFAULT_STACK_SIZE = 1048576; // 1 MB
    private static final int MAX_STACK_SIZE = 16 * 1024 * 1024; // 16 MB
    private static final int STACK_WINDOW = 8;
    private static final int NULL_REF = 0;
    private static final int DATA_BASE = 0x00001000;
    private static final int HEAP_BASE = 0x10000000;
    private static final long ADDRESS_SPACE_SIZE = 1L << 32;

    private final byte[] stack;
    private final Map<Integer, Instruction> code = new HashMap<>();
    private final Map<String, Integer> dataLabels = new HashMap<>();
    private final TreeMap<Integer, MemoryRegion> regions = new TreeMap<>(Integer::compareUnsigned);
    private final Map<String, Integer> labels = new HashMap<>();
    private final List<String> lines;
    private final List<CallFrame> callStack = new ArrayList<>();

    private int sp = 0;
    private int pc = 0;
    private int fp = 0;
    private int nextDataAddress = DATA_BASE;
    private int nextHeapAddress = HEAP_BASE;
    private int programEndAddress = 0;
    private boolean halted = false;

    /**
     * Starts the VM for a bytecode file.
     *
     * @param args first argument is the bytecode file path
     */
    public static void main(String[] args) {
        if (args.length == 0 || containsHelpOption(args)) {
            printUsage(System.out);
            return;
        }

        VMOptions options;
        try {
            options = parseOptions(args);
        } catch (OptionParseException e) {
            System.err.println(e.getMessage());
            printUsage(System.err);
            return;
        }

        VM vm = null;
        try {
            vm = new VM(options.filePath(), options.stackSizeBytes());
            vm.run();
        } catch (ParseException e) {
            System.err.println("Program contains parse errors. Execution aborted.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (VMExecutionException e) {
            System.err.println("Runtime error: " + e.getMessage());
            if (options.dumpOnRuntimeError() && vm != null) {
                vm.dumpState(System.err);
            }
        }
    }

    private static boolean containsHelpOption(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static void printUsage(PrintStream out) {
        out.println("Usage: java VM [options] <filePath>");
        out.println("Options:");
        out.println("  --stack-size <size>    Stack size in bytes, K/KB or M/MB (default 1M, max 16M)");
        out.println("  --dump-on-error        Print VM state dump after a runtime error");
        out.println("  -h, --help             Show this help");
    }

    private static VMOptions parseOptions(String[] args) throws OptionParseException {
        int stackSizeBytes = DEFAULT_STACK_SIZE;
        boolean dumpOnRuntimeError = false;
        String filePath = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ("--stack-size".equals(arg)) {
                if (i + 1 >= args.length) {
                    throw new OptionParseException("Missing value for --stack-size");
                }
                stackSizeBytes = parseStackSize(args[++i]);
            } else if (arg.startsWith("--stack-size=")) {
                stackSizeBytes = parseStackSize(arg.substring("--stack-size=".length()));
            } else if ("--dump-on-error".equals(arg)) {
                dumpOnRuntimeError = true;
            } else if (arg.startsWith("-")) {
                throw new OptionParseException("Unknown option: " + arg);
            } else if (filePath == null) {
                filePath = arg;
            } else {
                throw new OptionParseException("Unexpected argument: " + arg);
            }
        }

        if (filePath == null) {
            throw new OptionParseException("Missing bytecode file path");
        }

        return new VMOptions(filePath, stackSizeBytes, dumpOnRuntimeError);
    }

    private static int parseStackSize(String rawValue) throws OptionParseException {
        String value = rawValue.trim().replace("_", "");
        if (value.isEmpty()) {
            throw new OptionParseException("Invalid --stack-size value: " + rawValue);
        }

        String upper = value.toUpperCase(Locale.ROOT);
        long multiplier = 1;
        String number = upper;
        String substring = upper.substring(0, upper.length() - 2);
        if (upper.endsWith("KB")) {
            multiplier = 1024;
            number = substring;
        } else if (upper.endsWith("K")) {
            multiplier = 1024;
            number = upper.substring(0, upper.length() - 1);
        } else if (upper.endsWith("MB")) {
            multiplier = 1024 * 1024;
            number = substring;
        } else if (upper.endsWith("M")) {
            multiplier = 1024 * 1024;
            number = upper.substring(0, upper.length() - 1);
        }

        try {
            long stackSize = Math.multiplyExact(Long.parseLong(number), multiplier);
            if (stackSize < 1 || stackSize > MAX_STACK_SIZE) {
                throw new OptionParseException(
                        "--stack-size must be between 1 and " + MAX_STACK_SIZE + " bytes"
                );
            }
            return (int) stackSize;
        } catch (NumberFormatException | ArithmeticException e) {
            throw new OptionParseException("Invalid --stack-size value: " + rawValue);
        }
    }

    /**
     * Loads and parses a bytecode program.
     *
     * @param filePath path to the bytecode file
     * @throws IOException    if the bytecode file cannot be read
     * @throws ParseException if bytecode parsing fails
     */
    public VM(String filePath) throws IOException, ParseException {
        this(filePath, DEFAULT_STACK_SIZE);
    }

    /**
     * Loads and parses a bytecode program with a custom VM stack size.
     *
     * @param filePath       path to the bytecode file
     * @param stackSizeBytes stack size in bytes
     * @throws IOException    if the bytecode file cannot be read
     * @throws ParseException if bytecode parsing fails
     */
    public VM(String filePath, int stackSizeBytes) throws IOException, ParseException {
        validateStackSize(stackSizeBytes);
        this.stack = new byte[stackSizeBytes];
        this.lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        parse();
    }

    private static void validateStackSize(int stackSizeBytes) {
        if (stackSizeBytes < 1 || stackSizeBytes > MAX_STACK_SIZE) {
            throw new IllegalArgumentException(
                    "stackSizeBytes must be between 1 and " + MAX_STACK_SIZE + " bytes"
            );
        }
    }

    private void dumpStackWindow(PrintStream out) {
        out.println("=== STACK WINDOW (sp +/- " + VM.STACK_WINDOW + ") ===");

        int start = Math.max(0, sp - VM.STACK_WINDOW);
        int end = Math.min(stack.length, sp + VM.STACK_WINDOW);

        for (int i = start; i < end; i++) {
            int unsigned = stack[i] & 0xFF;

            String spMarker = (i == sp) ? "<-- sp" : "";
            String state = (i < sp) ? "VAL" : "INV";

            out.printf("[%05d] 0x%02X (%d) %s %s%n", i, unsigned, stack[i], state, spMarker);
        }
    }

    private void dumpHeap(PrintStream out) {
        out.println("=== HEAP DUMP ===");
        List<MemoryRegion> heapRegions = regions.values().stream()
                .filter(MemoryRegion::writable)
                .toList();
        out.println("objects = " + heapRegions.size());

        if (heapRegions.isEmpty()) {
            out.println("<empty>");
            return;
        }

        for (MemoryRegion region : heapRegions) {
            out.print(region.name() + " @ " + formatAddress(region.base()) + " = ");
            dumpByteArrayInline(region.bytes(), out);
        }
    }

    private void dumpData(PrintStream out) {
        out.println("=== DATA DUMP ===");

        if (dataLabels.isEmpty()) {
            out.println("<empty>");
            return;
        }

        for (Map.Entry<String, Integer> entry : dataLabels.entrySet()) {
            MemoryRegion region = regions.get(entry.getValue());
            out.print(entry.getKey() + " @ " + formatAddress(entry.getValue()) + " = ");
            dumpByteArrayInline(region.bytes(), out);
        }
    }

    private void dumpLabels(PrintStream out) {
        out.println("=== LABELS DUMP ===");

        if (labels.isEmpty()) {
            out.println("<empty>");
            return;
        }

        for (Map.Entry<String, Integer> entry : labels.entrySet()) {
            out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    private void dumpState(PrintStream out) {
        out.println("========================================");
        out.println("VM STATE DUMP");
        out.println("pc = " + pc);
        out.println("sp = " + sp);
        out.println("halted = " + halted);
        out.println("programEndAddress = " + programEndAddress);
        out.println("========================================");

        dumpStackWindow(out);
        out.println();

        dumpHeap(out);
        out.println();

        dumpData(out);
        out.println();

        dumpLabels(out);
        out.println("========================================");
    }

    private void dumpByteArrayInline(byte[] arr, PrintStream out) {
        if (arr == null) {
            out.println("<null>");
            return;
        }

        if (arr.length == 0) {
            out.println("[]");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(String.format("0x%02X", arr[i] & 0xFF));
        }
        sb.append("]");

        sb.append("  ascii=\"");
        for (byte b : arr) {
            int c = b & 0xFF;
            if (c >= 32 && c <= 126) {
                sb.append((char) c);
            } else {
                sb.append('.');
            }
        }
        sb.append("\"");

        out.println(sb);
    }

    private void validateEnterInstructions(int codeLineIndex, int dataLineIndex, List<String> errors) {
        // Track which labels are followed by ENTER
        String lastLabelName = null;
        boolean lastWasLabel = false;
        String currentFunctionLabel = null;
        boolean enterSeenInCurrentFunction = false;

        for (int i = codeLineIndex + 1; i < dataLineIndex; i++) {
            String line = stripComment(lines.get(i)).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (isLabel(line)) {
                lastLabelName = extractLabelName(line);
                lastWasLabel = true;
                // If we encounter a new function label, reset the ENTER flag
                if (lastLabelName.startsWith("_")) {
                    currentFunctionLabel = lastLabelName;
                    enterSeenInCurrentFunction = false;
                }
            } else {
                String instrName = line.split(",")[0].trim().toUpperCase();

                if ("ENTER".equals(instrName)) {
                    // ENTER is only allowed directly after a function label (starting with _)
                    if (!lastWasLabel || lastLabelName == null || !lastLabelName.startsWith("_")) {
                        errors.add("Line " + (i + 1) + ": ENTER must come directly after a function label (starting with _)");
                    }
                    if (enterSeenInCurrentFunction) {
                        errors.add("Line " + (i + 1) + ": multiple ENTER instructions in function '" + currentFunctionLabel + "' are not allowed");
                    }
                    enterSeenInCurrentFunction = true;
                }
                // If we see any other instruction after ENTER, we can have no more ENTER in this function
                lastWasLabel = false;
            }
        }
    }

    private void parse() throws ParseException {
        List<String> errors = new ArrayList<>();
        List<PendingJumpCheck> pendingJumpChecks = new ArrayList<>();

        int codeLineIndex = -1;
        int dataLineIndex = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = stripComment(lines.get(i)).trim();
            switch (line) {
                case "" -> {
                }
                case ".code" -> {
                    if (codeLineIndex != -1) {
                        errors.add("Line " + (i + 1) + ": duplicate '.code' section");
                    } else {
                        codeLineIndex = i;
                    }
                }
                case ".data" -> {
                    if (codeLineIndex == -1) {
                        errors.add("Line " + (i + 1) + ": '.data' section must appear after '.code'");
                    } else if (dataLineIndex != -1) {
                        errors.add("Line " + (i + 1) + ": duplicate '.data' section");
                    } else {
                        dataLineIndex = i;
                    }
                }
            }
        }

        if (codeLineIndex == -1) {
            errors.add("Missing required '.code' section");
        }
        if (dataLineIndex == -1) {
            errors.add("Missing required '.data' section");
        }

        if (!errors.isEmpty()) {
            printErrors(errors);
            throw new ParseException(errors);
        }

        int address = 0;

        for (int i = codeLineIndex + 1; i < dataLineIndex; i++) {
            String line = stripComment(lines.get(i)).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equals(".code") || line.equals(".data")) {
                errors.add("Line " + (i + 1) + ": nested section marker not allowed inside code section");
                continue;
            }

            if (isLabel(line)) {
                String labelName = extractLabelName(line);
                if (labels.containsKey(labelName)) {
                    errors.add("Line " + (i + 1) + ": duplicate label '" + labelName + "'");
                } else {
                    labels.put(labelName, address);
                }
            } else {
                address++;
            }
        }

        programEndAddress = address;

        address = 0;
        for (int i = codeLineIndex + 1; i < dataLineIndex; i++) {
            String line = stripComment(lines.get(i)).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (isLabel(line)) {
                continue;
            }

            try {
                Instruction instruction = parseInstruction(line, i + 1, pendingJumpChecks);
                code.put(address++, instruction);
            } catch (ParseException e) {
                errors.addAll(e.getErrors());
            }
        }

        // Validate ENTER instructions: must come directly after function label
        validateEnterInstructions(codeLineIndex, dataLineIndex, errors);

        for (int i = dataLineIndex + 1; i < lines.size(); i++) {
            String line = stripComment(lines.get(i)).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equals(".code") || line.equals(".data")) {
                errors.add("Line " + (i + 1) + ": section marker not allowed inside data section");
                continue;
            }

            if (isLabel(line)) {
                errors.add("Line " + (i + 1) + ": labels are only allowed in code section");
                continue;
            }

            try {
                parseDataLine(line, i + 1);
            } catch (ParseException e) {
                errors.addAll(e.getErrors());
            }
        }

        for (PendingJumpCheck check : pendingJumpChecks) {
            if (!labels.containsKey(check.labelName())) {
                errors.add("Line " + check.lineNumber() + ": unknown label '" + check.labelName() + "'");
            }
        }

        code.put(programEndAddress, new HaltInstruction());

        if (!errors.isEmpty()) {
            printErrors(errors);
            throw new ParseException(errors);
        }
    }

    private void printErrors(List<String> errors) {
        for (String error : errors) {
            System.err.println(error);
        }
    }

    private String stripComment(String line) {
        int index = line.indexOf("//");
        if (index >= 0) {
            return line.substring(0, index);
        }
        return line;
    }

    private boolean isLabel(String line) {
        return line.matches("^(_?[a-zA-Z][a-zA-Z0-9_]*):$");
    }

    private String extractLabelName(String line) {
        return line.substring(0, line.length() - 1);
    }

    private Instruction parseInstruction(String line, int lineNumber, List<PendingJumpCheck> pendingJumpChecks)
            throws ParseException {

        String[] parts = splitOperands(line);
        if (parts.length == 0 || parts[0].isBlank()) {
            throw new ParseException("Line " + lineNumber + ": empty instruction");
        }

        String instrName = parts[0].trim();
        InstructionKind kind = getInstructionKind(instrName);

        if (kind == null) {
            throw new ParseException("Line " + lineNumber + ": unknown instruction '" + instrName + "'");
        }

        return switch (kind) {
            case PUSHB -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new PushByteInstruction(parseByteOperand(parts[1], instrName, lineNumber));
            }
            case PUSHC -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new PushCharInstruction(parseCharOperand(parts[1], instrName, lineNumber));
            }
            case PUSHI -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new PushIntInstruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case PUSHD -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new PushDoubleInstruction(parseDoubleOperand(parts[1], instrName, lineNumber));
            }
            case PUSHR -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new PushReferenceInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }

            case LOADB -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new LoadByteFromDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case LOADC -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new LoadCharFromDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case LOADI -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new LoadIntFromDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case LOADD -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new LoadDoubleFromDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }

            case DLOAD1 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DLoad1Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DLOAD2 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DLoad2Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DLOAD4 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DLoad4Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DLOAD8 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DLoad8Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }

            case STOREB -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new StoreByteToDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case STOREC -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new StoreCharToDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case STOREI -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new StoreIntToDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }
            case STORED -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                yield new StoreDoubleToDataInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber),
                        parseIntOperand(parts[2], instrName, lineNumber)
                );
            }

            case DSTORE1 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DStore1Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DSTORE2 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DStore2Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DSTORE4 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DStore4Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DSTORE8 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DStore8Instruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }
            case DCOPYH -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new DataCopyToHeapInstruction(
                        parseIdentifier(parts[1], instrName, "data name", lineNumber)
                );
            }

            case POPB -> noOperand(parts, instrName, lineNumber, new PopByteInstruction());
            case POPC -> noOperand(parts, instrName, lineNumber, new PopCharInstruction());
            case POPI -> noOperand(parts, instrName, lineNumber, new PopIntInstruction());
            case POPD -> noOperand(parts, instrName, lineNumber, new PopDoubleInstruction());

            case LOAD1 -> noOperand(parts, instrName, lineNumber, new Load1Instruction());
            case LOAD2 -> noOperand(parts, instrName, lineNumber, new Load2Instruction());
            case LOAD4 -> noOperand(parts, instrName, lineNumber, new Load4Instruction());
            case LOAD8 -> noOperand(parts, instrName, lineNumber, new Load8Instruction());
            case STORE1 -> noOperand(parts, instrName, lineNumber, new Store1Instruction());
            case STORE2 -> noOperand(parts, instrName, lineNumber, new Store2Instruction());
            case STORE4 -> noOperand(parts, instrName, lineNumber, new Store4Instruction());
            case STORE8 -> noOperand(parts, instrName, lineNumber, new Store8Instruction());
            case NEW -> noOperand(parts, instrName, lineNumber, new NewInstruction());

            case IADD -> noOperand(parts, instrName, lineNumber, new IAddInstruction());
            case ISUB -> noOperand(parts, instrName, lineNumber, new ISubInstruction());
            case IMUL -> noOperand(parts, instrName, lineNumber, new IMulInstruction());
            case IDIV -> noOperand(parts, instrName, lineNumber, new IDivInstruction());
            case IMOD -> noOperand(parts, instrName, lineNumber, new IModInstruction());
            case DADD -> noOperand(parts, instrName, lineNumber, new DAddInstruction());
            case DSUB -> noOperand(parts, instrName, lineNumber, new DSubInstruction());
            case DMUL -> noOperand(parts, instrName, lineNumber, new DMulInstruction());
            case DDIV -> noOperand(parts, instrName, lineNumber, new DDivInstruction());

            case ILT -> noOperand(parts, instrName, lineNumber, new ILtInstruction());
            case ILE -> noOperand(parts, instrName, lineNumber, new ILeInstruction());
            case IGT -> noOperand(parts, instrName, lineNumber, new IGtInstruction());
            case IGE -> noOperand(parts, instrName, lineNumber, new IGeInstruction());
            case IEQ -> noOperand(parts, instrName, lineNumber, new IEqInstruction());
            case INE -> noOperand(parts, instrName, lineNumber, new INeInstruction());
            case DLT -> noOperand(parts, instrName, lineNumber, new DLtInstruction());
            case DLE -> noOperand(parts, instrName, lineNumber, new DLeInstruction());
            case DGT -> noOperand(parts, instrName, lineNumber, new DGtInstruction());
            case DGE -> noOperand(parts, instrName, lineNumber, new DGeInstruction());
            case DEQ -> noOperand(parts, instrName, lineNumber, new DEqInstruction());
            case DNE -> noOperand(parts, instrName, lineNumber, new DNeInstruction());

            case ISHL -> noOperand(parts, instrName, lineNumber, new IShlInstruction());
            case ISHR -> noOperand(parts, instrName, lineNumber, new IShrInstruction());

            case IAND -> noOperand(parts, instrName, lineNumber, new IAndInstruction());
            case IOR -> noOperand(parts, instrName, lineNumber, new IOrInstruction());
            case IXOR -> noOperand(parts, instrName, lineNumber, new IXorInstruction());

            case INEG -> noOperand(parts, instrName, lineNumber, new INegInstruction());
            case DNEG -> noOperand(parts, instrName, lineNumber, new DNegInstruction());
            case IINV -> noOperand(parts, instrName, lineNumber, new IInvInstruction());

            case B2I -> noOperand(parts, instrName, lineNumber, new B2IInstruction());
            case C2I -> noOperand(parts, instrName, lineNumber, new C2IInstruction());
            case I2D -> noOperand(parts, instrName, lineNumber, new I2DInstruction());
            case D2I -> noOperand(parts, instrName, lineNumber, new D2IInstruction());
            case I2B -> noOperand(parts, instrName, lineNumber, new I2BInstruction());
            case I2C -> noOperand(parts, instrName, lineNumber, new I2CInstruction());

            case DUPB -> noOperand(parts, instrName, lineNumber, new DupByteInstruction());
            case DUPC -> noOperand(parts, instrName, lineNumber, new DupCharInstruction());
            case DUPI -> noOperand(parts, instrName, lineNumber, new DupIntInstruction());
            case DUPD -> noOperand(parts, instrName, lineNumber, new DupDoubleInstruction());

            case JUMP -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                String label = parseLabelOperand(parts[1], instrName, lineNumber);
                if (label.startsWith("_")) {
                    throw new ParseException("Line " + lineNumber + ": JUMP must target a normal label (without _), got '" + label + "'");
                }
                pendingJumpChecks.add(new PendingJumpCheck(label, lineNumber));
                yield new JumpInstruction(label);
            }
            case JUMPT -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                String label = parseLabelOperand(parts[1], instrName, lineNumber);
                if (label.startsWith("_")) {
                    throw new ParseException("Line " + lineNumber + ": JUMPT must target a normal label (without _), got '" + label + "'");
                }
                pendingJumpChecks.add(new PendingJumpCheck(label, lineNumber));
                yield new JumpTrueInstruction(label);
            }
            case JUMPF -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                String label = parseLabelOperand(parts[1], instrName, lineNumber);
                if (label.startsWith("_")) {
                    throw new ParseException("Line " + lineNumber + ": JUMPF must target a normal label (without _), got '" + label + "'");
                }
                pendingJumpChecks.add(new PendingJumpCheck(label, lineNumber));
                yield new JumpFalseInstruction(label);
            }

            case CALL -> {
                ensureOperandCount(parts, 3, instrName, lineNumber);
                String label = parseLabelOperand(parts[1], instrName, lineNumber);
                if (!label.startsWith("_")) {
                    throw new ParseException("Line " + lineNumber + ": CALL must target a function label (starting with _), got '" + label + "'");
                }

                int argBytes = parseIntOperand(parts[2], instrName, lineNumber);
                if (argBytes < 0) {
                    throw new ParseException("Line " + lineNumber + ": CALL argBytes must be non-negative, got " + argBytes);
                }

                pendingJumpChecks.add(new PendingJumpCheck(label, lineNumber));
                yield new CallInstruction(label, argBytes);
            }

            case ENTER -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                int size = parseIntOperand(parts[1], instrName, lineNumber);
                if (size < 0) {
                    throw new ParseException("Line " + lineNumber + ": ENTER size must be non-negative, got " + size);
                }
                yield new EnterInstruction(size);
            }

            case RET -> noOperand(parts, instrName, lineNumber, new RetInstruction());
            case RETB -> noOperand(parts, instrName, lineNumber, new RetByteInstruction());
            case RETC -> noOperand(parts, instrName, lineNumber, new RetCharInstruction());
            case RETI -> noOperand(parts, instrName, lineNumber, new RetIntInstruction());
            case RETD -> noOperand(parts, instrName, lineNumber, new RetDoubleInstruction());

            case FLOAD1 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FLoad1Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FLOAD2 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FLoad2Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FLOAD4 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FLoad4Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FLOAD8 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FLoad8Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }

            case FSTORE1 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FStore1Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FSTORE2 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FStore2Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FSTORE4 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FStore4Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }
            case FSTORE8 -> {
                ensureOperandCount(parts, 2, instrName, lineNumber);
                yield new FStore8Instruction(parseIntOperand(parts[1], instrName, lineNumber));
            }

            case HALT -> noOperand(parts, instrName, lineNumber, new HaltInstruction());
            case PRINTB -> noOperand(parts, instrName, lineNumber, new PrintByteInstruction());
            case PRINTC -> noOperand(parts, instrName, lineNumber, new PrintCharInstruction());
            case PRINTI -> noOperand(parts, instrName, lineNumber, new PrintIntInstruction());
            case PRINTD -> noOperand(parts, instrName, lineNumber, new PrintDoubleInstruction());
            case PRINTS -> noOperand(parts, instrName, lineNumber, new PrintStringInstruction());
        };
    }

    private String[] splitOperands(String line) {
        String[] raw = line.split(",");
        String[] trimmed = new String[raw.length];
        for (int i = 0; i < raw.length; i++) {
            trimmed[i] = raw[i].trim();
        }
        return trimmed;
    }

    private void parseDataLine(String line, int lineNumber) throws ParseException {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            throw new ParseException("Line " + lineNumber + ": invalid data declaration");
        }

        String name = parts[0];
        if (dataLabels.containsKey(name)) {
            throw new ParseException("Line " + lineNumber + ": duplicate data symbol '" + name + "'");
        }

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

        int base = allocateDataRegion(name, dataBytes);
        dataLabels.put(name, base);
    }

    private void ensureOperandCount(String[] parts, int expectedCount, String instrName, int lineNumber)
            throws ParseException {
        if (parts.length != expectedCount) {
            throw new ParseException(
                    "Line " + lineNumber + ": instruction '" + instrName + "' expects "
                            + (expectedCount - 1) + " operand(s), got " + (parts.length - 1)
            );
        }
    }

    private Instruction noOperand(String[] parts, String instrName, int lineNumber, Instruction instruction)
            throws ParseException {
        ensureOperandCount(parts, 1, instrName, lineNumber);
        return instruction;
    }

    private byte parseByteOperand(String operand, String instrName, int lineNumber) throws ParseException {
        try {
            return Byte.parseByte(operand.trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid byte operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private char parseCharOperand(String operand, String instrName, int lineNumber) throws ParseException {
        try {
            return (char) Integer.parseInt(operand.trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid char operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private int parseIntOperand(String operand, String instrName, int lineNumber) throws ParseException {
        try {
            return Integer.parseInt(operand.trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid int operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private double parseDoubleOperand(String operand, String instrName, int lineNumber) throws ParseException {
        try {
            return Double.parseDouble(operand.trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Line " + lineNumber + ": invalid double operand '" + operand + "' for instruction '" + instrName + "'");
        }
    }

    private String parseLabelOperand(String operand, String instrName, int lineNumber) throws ParseException {
        String value = operand.trim();
        if (value.isEmpty()) {
            throw new ParseException("Line " + lineNumber + ": missing label operand for instruction '" + instrName + "'");
        }
        return value;
    }

    private String parseIdentifier(String operand, String instrName, String description, int lineNumber)
            throws ParseException {
        String value = operand.trim();
        if (value.isEmpty()) {
            throw new ParseException("Line " + lineNumber + ": missing " + description + " for instruction '" + instrName + "'");
        }
        return value;
    }

    private InstructionKind getInstructionKind(String name) {
        return switch (name.toUpperCase()) {
            case "PUSHB" -> InstructionKind.PUSHB;
            case "PUSHC" -> InstructionKind.PUSHC;
            case "PUSHI" -> InstructionKind.PUSHI;
            case "PUSHD" -> InstructionKind.PUSHD;
            case "PUSHR" -> InstructionKind.PUSHR;

            case "LOADB" -> InstructionKind.LOADB;
            case "LOADC" -> InstructionKind.LOADC;
            case "LOADI" -> InstructionKind.LOADI;
            case "LOADD" -> InstructionKind.LOADD;

            case "DLOAD1" -> InstructionKind.DLOAD1;
            case "DLOAD2" -> InstructionKind.DLOAD2;
            case "DLOAD4" -> InstructionKind.DLOAD4;
            case "DLOAD8" -> InstructionKind.DLOAD8;

            case "STOREB" -> InstructionKind.STOREB;
            case "STOREC" -> InstructionKind.STOREC;
            case "STOREI" -> InstructionKind.STOREI;
            case "STORED" -> InstructionKind.STORED;

            case "DSTORE1" -> InstructionKind.DSTORE1;
            case "DSTORE2" -> InstructionKind.DSTORE2;
            case "DSTORE4" -> InstructionKind.DSTORE4;
            case "DSTORE8" -> InstructionKind.DSTORE8;
            case "DCOPYH" -> InstructionKind.DCOPYH;

            case "FLOAD1" -> InstructionKind.FLOAD1;
            case "FLOAD2" -> InstructionKind.FLOAD2;
            case "FLOAD4" -> InstructionKind.FLOAD4;
            case "FLOAD8" -> InstructionKind.FLOAD8;

            case "FSTORE1" -> InstructionKind.FSTORE1;
            case "FSTORE2" -> InstructionKind.FSTORE2;
            case "FSTORE4" -> InstructionKind.FSTORE4;
            case "FSTORE8" -> InstructionKind.FSTORE8;

            case "POPB" -> InstructionKind.POPB;
            case "POPC" -> InstructionKind.POPC;
            case "POPI" -> InstructionKind.POPI;
            case "POPD" -> InstructionKind.POPD;

            case "LOAD1" -> InstructionKind.LOAD1;
            case "LOAD2" -> InstructionKind.LOAD2;
            case "LOAD4" -> InstructionKind.LOAD4;
            case "LOAD8" -> InstructionKind.LOAD8;
            case "STORE1" -> InstructionKind.STORE1;
            case "STORE2" -> InstructionKind.STORE2;
            case "STORE4" -> InstructionKind.STORE4;
            case "STORE8" -> InstructionKind.STORE8;
            case "NEW" -> InstructionKind.NEW;

            case "IADD" -> InstructionKind.IADD;
            case "ISUB" -> InstructionKind.ISUB;
            case "IMUL" -> InstructionKind.IMUL;
            case "IDIV" -> InstructionKind.IDIV;
            case "IMOD" -> InstructionKind.IMOD;
            case "DADD" -> InstructionKind.DADD;
            case "DSUB" -> InstructionKind.DSUB;
            case "DMUL" -> InstructionKind.DMUL;
            case "DDIV" -> InstructionKind.DDIV;

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

            case "ISHL" -> InstructionKind.ISHL;
            case "ISHR" -> InstructionKind.ISHR;

            case "IAND" -> InstructionKind.IAND;
            case "IOR" -> InstructionKind.IOR;
            case "IXOR" -> InstructionKind.IXOR;

            case "INEG" -> InstructionKind.INEG;
            case "DNEG" -> InstructionKind.DNEG;
            case "IINV" -> InstructionKind.IINV;

            case "B2I" -> InstructionKind.B2I;
            case "C2I" -> InstructionKind.C2I;
            case "I2D" -> InstructionKind.I2D;
            case "D2I" -> InstructionKind.D2I;
            case "I2B" -> InstructionKind.I2B;
            case "I2C" -> InstructionKind.I2C;

            case "DUPB" -> InstructionKind.DUPB;
            case "DUPC" -> InstructionKind.DUPC;
            case "DUPI" -> InstructionKind.DUPI;
            case "DUPD" -> InstructionKind.DUPD;

            case "JUMP" -> InstructionKind.JUMP;
            case "JUMPT" -> InstructionKind.JUMPT;
            case "JUMPF" -> InstructionKind.JUMPF;

            case "CALL" -> InstructionKind.CALL;
            case "ENTER" -> InstructionKind.ENTER;

            case "RET" -> InstructionKind.RET;
            case "RETB" -> InstructionKind.RETB;
            case "RETC" -> InstructionKind.RETC;
            case "RETI" -> InstructionKind.RETI;
            case "RETD" -> InstructionKind.RETD;

            case "HALT" -> InstructionKind.HALT;
            case "PRINTB" -> InstructionKind.PRINTB;
            case "PRINTC" -> InstructionKind.PRINTC;
            case "PRINTI" -> InstructionKind.PRINTI;
            case "PRINTD" -> InstructionKind.PRINTD;
            case "PRINTS" -> InstructionKind.PRINTS;

            default -> null;
        };
    }

    /**
     * Executes the loaded program from the {@code _main} label until HALT or
     * until no instruction exists at the current program counter.
     */
    public void run() {
        if (labels.containsKey("_main")) {
            callStack.add(new CallFrame(-1, 0, 0, 0));
            pc = labels.get("_main");
        } else {
            throw new VMExecutionException("No _main function found");
        }

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

    private void ensureStackCapacity(int bytesToPush) {
        if (sp + bytesToPush > stack.length) {
            throw new VMExecutionException("Stack overflow while pushing " + bytesToPush + " byte(s)");
        }
    }

    private void ensureStackAvailable(int bytesToPop, String typeName) {
        if (sp < bytesToPop) {
            throw new VMExecutionException("Stack underflow on " + typeName + " pop");
        }
    }

    private void pushByte(byte val) {
        ensureStackCapacity(1);
        stack[sp++] = val;
    }

    private byte popByte() {
        ensureStackAvailable(1, "byte");
        return stack[--sp];
    }

    private void pushChar(char val) {
        ensureStackCapacity(2);
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
    }

    private char popChar() {
        ensureStackAvailable(2, "char");
        byte high = stack[--sp];
        byte low = stack[--sp];
        return (char) (((high & 0xFF) << 8) | (low & 0xFF));
    }

    private void pushInt(int val) {
        ensureStackCapacity(4);
        stack[sp++] = (byte) (val & 0xFF);
        stack[sp++] = (byte) ((val >> 8) & 0xFF);
        stack[sp++] = (byte) ((val >> 16) & 0xFF);
        stack[sp++] = (byte) ((val >> 24) & 0xFF);
    }

    private int popInt() {
        ensureStackAvailable(4, "int");
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
        ensureStackCapacity(8);
        long bits = Double.doubleToLongBits(val);
        for (int i = 0; i < 8; i++) {
            stack[sp++] = (byte) (bits & 0xFF);
            bits >>= 8;
        }
    }

    private double popDouble() {
        ensureStackAvailable(8, "double");
        long bits = 0;
        for (int i = 7; i >= 0; i--) {
            bits |= ((long) (stack[--sp] & 0xFF)) << (8 * i);
        }
        return Double.longBitsToDouble(bits);
    }

    private int makeDataReference(String name) {
        Integer address = dataLabels.get(name);
        if (address == null) {
            throw new VMExecutionException("Data object '" + name + "' not found");
        }
        return address;
    }

    private int allocateDataRegion(String name, byte[] bytes) {
        int base = nextDataAddress;
        long end = Integer.toUnsignedLong(base) + Math.max(bytes.length, 1);
        long heapStart = Integer.toUnsignedLong(HEAP_BASE);
        if (end > heapStart) {
            throw new VMExecutionException("Data section exceeds reserved address range");
        }

        addRegion(new MemoryRegion(base, bytes.length, false, "data:" + name, bytes));
        nextDataAddress = (int) end;
        return base;
    }

    private int allocateHeapRegion(int size) {
        if (size < 0) {
            throw new VMExecutionException("Negative heap allocation size: " + size);
        }

        int base = nextHeapAddress;
        long end = Integer.toUnsignedLong(base) + Math.max(size, 1);
        if (end > ADDRESS_SPACE_SIZE) {
            throw new VMExecutionException("Heap address space exhausted");
        }

        addRegion(new MemoryRegion(base, size, true, "heap:" + formatAddress(base), new byte[size]));
        nextHeapAddress = (int) end;
        return base;
    }

    private void addRegion(MemoryRegion region) {
        long base = Integer.toUnsignedLong(region.base());
        long end = base + Math.max(region.size(), 0);

        Map.Entry<Integer, MemoryRegion> previous = regions.floorEntry(region.base());
        if (previous != null && previous.getKey().equals(region.base())) {
            throw new VMExecutionException("Duplicate memory region at " + formatAddress(region.base()));
        }
        if (previous != null && regionEnd(previous.getValue()) > base) {
            throw new VMExecutionException("Memory region overlap at " + formatAddress(region.base()));
        }

        Map.Entry<Integer, MemoryRegion> next = regions.ceilingEntry(region.base());
        if (next != null && end > Integer.toUnsignedLong(next.getKey())) {
            throw new VMExecutionException("Memory region overlap at " + formatAddress(region.base()));
        }

        regions.put(region.base(), region);
    }

    private long regionEnd(MemoryRegion region) {
        return Integer.toUnsignedLong(region.base()) + Math.max(region.size(), 0);
    }

    private String formatAddress(int address) {
        return "0x%08X".formatted(address);
    }

    private String describeAddress(int address) {
        if (address == NULL_REF) {
            return "null";
        }
        return formatAddress(address);
    }

    private int addAddressOffset(int base, int offset, String instructionName) {
        long target = Integer.toUnsignedLong(base) + (long) offset;
        if (target < 0 || target >= ADDRESS_SPACE_SIZE) {
            throw new VMExecutionException(
                    instructionName + ": address overflow (base=" + describeAddress(base) + ", offset=" + offset + ")"
            );
        }
        return (int) target;
    }

    private MemoryAccess resolveRegion(int address, int size) {
        if (size < 0) {
            throw new VMExecutionException("Negative memory access size: " + size);
        }
        if (address == NULL_REF) {
            throw new VMExecutionException("Null reference");
        }

        long start = Integer.toUnsignedLong(address);
        long end = start + size;
        if (end > ADDRESS_SPACE_SIZE) {
            throw new VMExecutionException("Memory access out of bounds (address=" + formatAddress(address) + ", size=" + size + ")");
        }

        Map.Entry<Integer, MemoryRegion> entry = regions.floorEntry(address);
        if (entry == null) {
            throw new VMExecutionException("Invalid memory address: " + formatAddress(address));
        }

        MemoryRegion region = entry.getValue();
        long regionBase = Integer.toUnsignedLong(region.base());
        long regionEnd = regionEnd(region);
        if (start < regionBase || end > regionEnd) {
            throw new VMExecutionException(
                    region.name() + " access out of bounds (address=" + formatAddress(address) + ", size=" + size + ")"
            );
        }

        return new MemoryAccess(region, (int) (start - regionBase));
    }

    private MemoryAccess resolveWritableRegion(int address, int size) {
        MemoryAccess access = resolveRegion(address, size);
        if (!access.region().writable()) {
            throw new VMExecutionException(access.region().name() + " is read-only");
        }
        return access;
    }

    private int checkFrameAccess(int offset, int size) {
        long addr = (long) fp + offset;
        if (size < 0 || addr < 0 || addr > stack.length - size) {
            throw new VMExecutionException(
                    "Frame access out of bounds: fp=" + fp + ", offset=" + offset + ", size=" + size
            );
        }
        return (int) addr;
    }

    private int dataAddress(String name, int offset, String instructionName) {
        return addAddressOffset(makeDataReference(name), offset, instructionName + " " + name);
    }

    private byte readByte(int address) {
        MemoryAccess access = resolveRegion(address, 1);
        return access.region().bytes()[access.offset()];
    }

    private char readChar(int address) {
        MemoryAccess access = resolveRegion(address, 2);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        byte low = bytes[offset];
        byte high = bytes[offset + 1];
        return (char) (((high & 0xFF) << 8) | (low & 0xFF));
    }

    private int readInt(int address) {
        MemoryAccess access = resolveRegion(address, 4);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= (bytes[offset + i] & 0xFF) << (8 * i);
        }
        return value;
    }

    private double readDouble(int address) {
        MemoryAccess access = resolveRegion(address, 8);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits |= ((long) (bytes[offset + i] & 0xFF)) << (8 * i);
        }
        return Double.longBitsToDouble(bits);
    }

    private void writeByte(int address, byte value) {
        MemoryAccess access = resolveWritableRegion(address, 1);
        access.region().bytes()[access.offset()] = value;
    }

    private void writeChar(int address, char value) {
        MemoryAccess access = resolveWritableRegion(address, 2);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        bytes[offset] = (byte) (value & 0xFF);
        bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private void writeInt(int address, int value) {
        MemoryAccess access = resolveWritableRegion(address, 4);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        for (int i = 0; i < 4; i++) {
            bytes[offset + i] = (byte) (value & 0xFF);
            value >>= 8;
        }
    }

    private void writeDouble(int address, double value) {
        MemoryAccess access = resolveWritableRegion(address, 8);
        byte[] bytes = access.region().bytes();
        int offset = access.offset();
        long bits = Double.doubleToLongBits(value);
        for (int i = 0; i < 8; i++) {
            bytes[offset + i] = (byte) (bits & 0xFF);
            bits >>= 8;
        }
    }

    private abstract static class Instruction {
        public abstract void execute(VM vm);
    }

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

    private static final class PushReferenceInstruction extends Instruction {
        private final String dataName;

        public PushReferenceInstruction(String dataName) {
            this.dataName = dataName;
        }

        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.makeDataReference(dataName));
        }
    }

    private static final class LoadByteFromDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public LoadByteFromDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            vm.pushByte(vm.readByte(vm.dataAddress(name, offset, "LOADB")));
        }
    }

    private static final class LoadCharFromDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public LoadCharFromDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            vm.pushChar(vm.readChar(vm.dataAddress(name, offset, "LOADC")));
        }
    }

    private static final class LoadIntFromDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public LoadIntFromDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            vm.pushInt(vm.readInt(vm.dataAddress(name, offset, "LOADI")));
        }
    }

    private static final class LoadDoubleFromDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public LoadDoubleFromDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            vm.pushDouble(vm.readDouble(vm.dataAddress(name, offset, "LOADD")));
        }
    }

    private static final class DLoad1Instruction extends Instruction {
        private final String name;

        public DLoad1Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            vm.pushByte(vm.readByte(vm.dataAddress(name, offset, "DLOAD1")));
        }
    }

    private static final class DLoad2Instruction extends Instruction {
        private final String name;

        public DLoad2Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            vm.pushChar(vm.readChar(vm.dataAddress(name, offset, "DLOAD2")));
        }
    }

    private static final class DLoad4Instruction extends Instruction {
        private final String name;

        public DLoad4Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            vm.pushInt(vm.readInt(vm.dataAddress(name, offset, "DLOAD4")));
        }
    }

    private static final class DLoad8Instruction extends Instruction {
        private final String name;

        public DLoad8Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            vm.pushDouble(vm.readDouble(vm.dataAddress(name, offset, "DLOAD8")));
        }
    }

    private static final class StoreByteToDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public StoreByteToDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();
            vm.writeByte(vm.dataAddress(name, offset, "STOREB"), value);
        }
    }

    private static final class StoreCharToDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public StoreCharToDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            char value = vm.popChar();
            vm.writeChar(vm.dataAddress(name, offset, "STOREC"), value);
        }
    }

    private static final class StoreIntToDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public StoreIntToDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int value = vm.popInt();
            vm.writeInt(vm.dataAddress(name, offset, "STOREI"), value);
        }
    }

    private static final class StoreDoubleToDataInstruction extends Instruction {
        private final String name;
        private final int offset;

        public StoreDoubleToDataInstruction(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            vm.writeDouble(vm.dataAddress(name, offset, "STORED"), vm.popDouble());
        }
    }

    private static final class DStore1Instruction extends Instruction {
        private final String name;

        public DStore1Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();
            int offset = vm.popInt();

            vm.writeByte(vm.dataAddress(name, offset, "DSTORE1"), value);
        }
    }

    private static final class DStore2Instruction extends Instruction {
        private final String name;

        public DStore2Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            char value = vm.popChar();
            int offset = vm.popInt();

            vm.writeChar(vm.dataAddress(name, offset, "DSTORE2"), value);
        }
    }

    private static final class DStore4Instruction extends Instruction {
        private final String name;

        public DStore4Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int value = vm.popInt();
            int offset = vm.popInt();

            vm.writeInt(vm.dataAddress(name, offset, "DSTORE4"), value);
        }
    }

    private static final class DStore8Instruction extends Instruction {
        private final String name;

        public DStore8Instruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            double value = vm.popDouble();
            int offset = vm.popInt();

            vm.writeDouble(vm.dataAddress(name, offset, "DSTORE8"), value);
        }
    }

    private static final class DataCopyToHeapInstruction extends Instruction {
        private final String name;

        public DataCopyToHeapInstruction(String name) {
            this.name = name;
        }

        @Override
        public void execute(VM vm) {
            int byteCount = vm.popInt();
            int offset = vm.popInt();
            int base = vm.popInt();

            if (byteCount < 0) {
                throw new VMExecutionException("DCOPYH " + name + ": negative byte count " + byteCount);
            }

            MemoryAccess source = vm.resolveRegion(vm.makeDataReference(name), byteCount);
            int targetAddress = vm.addAddressOffset(base, offset, "DCOPYH " + name);
            MemoryAccess target = vm.resolveWritableRegion(targetAddress, byteCount);

            System.arraycopy(
                    source.region().bytes(), source.offset(),
                    target.region().bytes(), target.offset(),
                    byteCount
            );
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

    private static final class Load1Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.pushByte(vm.readByte(vm.addAddressOffset(base, offset, "LOAD1")));
        }
    }

    private static final class Load2Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.pushChar(vm.readChar(vm.addAddressOffset(base, offset, "LOAD2")));
        }
    }

    private static final class Load4Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.pushInt(vm.readInt(vm.addAddressOffset(base, offset, "LOAD4")));
        }
    }

    private static final class Load8Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.pushDouble(vm.readDouble(vm.addAddressOffset(base, offset, "LOAD8")));
        }
    }

    private static final class Store1Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.writeByte(vm.addAddressOffset(base, offset, "STORE1"), value);
        }
    }

    private static final class Store2Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            char value = vm.popChar();
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.writeChar(vm.addAddressOffset(base, offset, "STORE2"), value);
        }
    }

    private static final class Store4Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int value = vm.popInt();
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.writeInt(vm.addAddressOffset(base, offset, "STORE4"), value);
        }
    }

    private static final class Store8Instruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double value = vm.popDouble();
            int offset = vm.popInt();
            int base = vm.popInt();
            vm.writeDouble(vm.addAddressOffset(base, offset, "STORE8"), value);
        }
    }

    private static final class NewInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int size = vm.popInt();
            vm.pushInt(vm.allocateHeapRegion(size));
        }
    }

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

    private static final class B2IInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            byte b = vm.popByte();
            vm.pushInt(b);
        }
    }

    private static final class C2IInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            char c = vm.popChar();
            vm.pushInt(c);
        }
    }

    private static final class I2DInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int i = vm.popInt();
            vm.pushDouble((double) i);
        }
    }

    private static final class D2IInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double d = vm.popDouble();
            vm.pushInt((int) d);
        }
    }

    private static final class I2BInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int i = vm.popInt();
            vm.pushByte((byte) i);
        }
    }

    private static final class I2CInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int i = vm.popInt();
            vm.pushChar((char) i);
        }
    }

    private static final class DupByteInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();
            vm.pushByte(value);
            vm.pushByte(value);
        }
    }

    private static final class DupCharInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            char value = vm.popChar();
            vm.pushChar(value);
            vm.pushChar(value);
        }
    }

    private static final class DupIntInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int value = vm.popInt();
            vm.pushInt(value);
            vm.pushInt(value);
        }
    }

    private static final class DupDoubleInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double value = vm.popDouble();
            vm.pushDouble(value);
            vm.pushDouble(value);
        }
    }

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

    private static final class HaltInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            vm.halt();
        }
    }

    private static final class PrintByteInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            System.out.print((char) vm.popByte());
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

    private static final class PrintStringInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int address = vm.popInt();
            vm.printNullTerminatedCharString(address, "PRINTS " + vm.describeAddress(address));
        }
    }

    private void printNullTerminatedCharString(int address, String sourceName) {
        MemoryAccess access = resolveRegion(address, 0);
        byte[] bytes = access.region().bytes();
        for (int offset = access.offset(); offset + 1 < bytes.length; offset += 2) {
            byte low = bytes[offset];
            byte high = bytes[offset + 1];

            char c = (char) (((high & 0xFF) << 8) | (low & 0xFF));

            if (c == '\0') {
                return;
            }

            System.out.print(c);
        }

        throw new VMExecutionException(sourceName + ": string is not null-terminated");
    }

    private static final class CallInstruction extends Instruction {
        private final String label;
        private final int argBytes;

        public CallInstruction(String label, int argBytes) {
            this.label = label;
            this.argBytes = argBytes;
        }

        @Override
        public void execute(VM vm) {
            if (argBytes > vm.sp) {
                throw new VMExecutionException("CALL: not enough bytes on stack for " + argBytes + " argument byte(s)");
            }

            int callerSp = vm.sp - argBytes;
            vm.callStack.add(new CallFrame(vm.pc + 1, vm.fp, callerSp, argBytes));
            vm.pc = vm.resolveLabel(label) - 1;
        }
    }

    private static final class EnterInstruction extends Instruction {
        private final int size;

        public EnterInstruction(int size) {
            this.size = size;
        }

        @Override
        public void execute(VM vm) {
            vm.fp = vm.sp;
            vm.ensureStackCapacity(size);
            vm.sp += size;
        }
    }

    private static final class RetInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            if (vm.callStack.isEmpty()) {
                throw new VMExecutionException("RET: call stack is empty");
            }

            CallFrame frame = vm.callStack.removeLast();

            vm.sp = frame.callerSp();
            vm.fp = frame.previousFp();

            if (frame.returnPc() == -1) {
                vm.halted = true;
            } else {
                vm.pc = frame.returnPc() - 1;
            }
        }
    }

    private static final class RetByteInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            byte value = vm.popByte();

            if (vm.callStack.isEmpty()) {
                throw new VMExecutionException("RETB: call stack is empty");
            }

            CallFrame frame = vm.callStack.removeLast();

            vm.sp = frame.callerSp();
            vm.fp = frame.previousFp();
            vm.pushByte(value);

            if (frame.returnPc() == -1) {
                vm.halted = true;
            } else {
                vm.pc = frame.returnPc() - 1;
            }
        }
    }

    private static final class RetCharInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            char value = vm.popChar();

            if (vm.callStack.isEmpty()) {
                throw new VMExecutionException("RETC: call stack is empty");
            }

            CallFrame frame = vm.callStack.removeLast();

            vm.sp = frame.callerSp();
            vm.fp = frame.previousFp();
            vm.pushChar(value);

            if (frame.returnPc() == -1) {
                vm.halted = true;
            } else {
                vm.pc = frame.returnPc() - 1;
            }
        }
    }

    private static final class RetIntInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            int value = vm.popInt();

            if (vm.callStack.isEmpty()) {
                throw new VMExecutionException("RETI: call stack is empty");
            }

            CallFrame frame = vm.callStack.removeLast();

            vm.sp = frame.callerSp();
            vm.fp = frame.previousFp();
            vm.pushInt(value);

            if (frame.returnPc() == -1) {
                vm.halted = true;
            } else {
                vm.pc = frame.returnPc() - 1;
            }
        }
    }

    private static final class RetDoubleInstruction extends Instruction {
        @Override
        public void execute(VM vm) {
            double value = vm.popDouble();

            if (vm.callStack.isEmpty()) {
                throw new VMExecutionException("RETD: call stack is empty");
            }

            CallFrame frame = vm.callStack.remove(vm.callStack.size() - 1);

            vm.sp = frame.callerSp();
            vm.fp = frame.previousFp();
            vm.pushDouble(value);

            if (frame.returnPc() == -1) {
                vm.halted = true;
            } else {
                vm.pc = frame.returnPc() - 1;
            }
        }
    }

    private static final class FLoad1Instruction extends Instruction {
        private final int offset;

        public FLoad1Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int addr = vm.checkFrameAccess(offset, 1);
            byte val = vm.stack[addr];
            vm.pushByte(val);
        }
    }

    private static final class FLoad2Instruction extends Instruction {
        private final int offset;

        public FLoad2Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int addr = vm.checkFrameAccess(offset, 2);
            byte b0 = vm.stack[addr];
            byte b1 = vm.stack[addr + 1];
            vm.pushChar((char) (((b1 & 0xFF) << 8) | (b0 & 0xFF)));
        }
    }

    private static final class FLoad4Instruction extends Instruction {
        private final int offset;

        public FLoad4Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int addr = vm.checkFrameAccess(offset, 4);
            byte b0 = vm.stack[addr];
            byte b1 = vm.stack[addr + 1];
            byte b2 = vm.stack[addr + 2];
            byte b3 = vm.stack[addr + 3];
            int val = ((b3 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b1 & 0xFF) << 8) | (b0 & 0xFF);
            vm.pushInt(val);
        }
    }

    private static final class FLoad8Instruction extends Instruction {
        private final int offset;

        public FLoad8Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int addr = vm.checkFrameAccess(offset, 8);
            long bits = 0;
            for (int i = 7; i >= 0; i--) {
                bits |= ((long) (vm.stack[addr + i] & 0xFF)) << (8 * i);
            }
            vm.pushDouble(Double.longBitsToDouble(bits));
        }
    }

    private static final class FStore1Instruction extends Instruction {
        private final int offset;

        public FStore1Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            byte val = vm.popByte();
            int addr = vm.checkFrameAccess(offset, 1);
            vm.stack[addr] = val;
        }
    }

    private static final class FStore2Instruction extends Instruction {
        private final int offset;

        public FStore2Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            char val = vm.popChar();
            int addr = vm.checkFrameAccess(offset, 2);
            vm.stack[addr] = (byte) (val & 0xFF);
            vm.stack[addr + 1] = (byte) ((val >> 8) & 0xFF);
        }
    }

    private static final class FStore4Instruction extends Instruction {
        private final int offset;

        public FStore4Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            int val = vm.popInt();
            int addr = vm.checkFrameAccess(offset, 4);
            vm.stack[addr] = (byte) (val & 0xFF);
            vm.stack[addr + 1] = (byte) ((val >> 8) & 0xFF);
            vm.stack[addr + 2] = (byte) ((val >> 16) & 0xFF);
            vm.stack[addr + 3] = (byte) ((val >> 24) & 0xFF);
        }
    }

    private static final class FStore8Instruction extends Instruction {
        private final int offset;

        public FStore8Instruction(int offset) {
            this.offset = offset;
        }

        @Override
        public void execute(VM vm) {
            double val = vm.popDouble();
            int addr = vm.checkFrameAccess(offset, 8);
            long bits = Double.doubleToLongBits(val);
            for (int i = 0; i < 8; i++) {
                vm.stack[addr + i] = (byte) (bits & 0xFF);
                bits >>= 8;
            }
        }
    }

    private enum InstructionKind {
        PUSHB, PUSHC, PUSHI, PUSHD, PUSHR,
        LOADB, LOADC, LOADI, LOADD,
        DLOAD1, DLOAD2, DLOAD4, DLOAD8,
        STOREB, STOREC, STOREI, STORED,
        DSTORE1, DSTORE2, DSTORE4, DSTORE8,
        DCOPYH,
        FLOAD1, FLOAD2, FLOAD4, FLOAD8,
        FSTORE1, FSTORE2, FSTORE4, FSTORE8,
        POPB, POPC, POPI, POPD,
        LOAD1, LOAD2, LOAD4, LOAD8,
        STORE1, STORE2, STORE4, STORE8,
        NEW,
        IADD, ISUB, IMUL, IDIV, IMOD,
        DADD, DSUB, DMUL, DDIV,
        ILT, ILE, IGT, IGE, IEQ, INE,
        DLT, DLE, DGT, DGE, DEQ, DNE,
        ISHL, ISHR,
        IAND, IOR, IXOR,
        INEG, DNEG, IINV,
        B2I, C2I, I2D, D2I, I2B, I2C,
        DUPB, DUPC, DUPI, DUPD,
        JUMP, JUMPT, JUMPF,
        CALL, ENTER,
        RET, RETB, RETC, RETI, RETD,
        HALT, PRINTB, PRINTC, PRINTI, PRINTD, PRINTS
    }

    private record PendingJumpCheck(String labelName, int lineNumber) {
    }

    private record VMOptions(String filePath, int stackSizeBytes, boolean dumpOnRuntimeError) {
    }

    private record MemoryRegion(int base, int size, boolean writable, String name, byte[] bytes) {
    }

    private record MemoryAccess(MemoryRegion region, int offset) {
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

    private static final class OptionParseException extends Exception {
        public OptionParseException(String message) {
            super(message);
        }
    }

    private static final class VMExecutionException extends RuntimeException {
        public VMExecutionException(String message) {
            super(message);
        }
    }

    private record CallFrame(int returnPc, int previousFp, int callerSp, int argBytes) {
    }

}
