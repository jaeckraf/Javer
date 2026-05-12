package ch.zhaw.it.pm4.javer.compiler;

import java.nio.file.Path;

public class CompilerOptions {

    private static final String SOURCE_FILE_EXTENSION = ".javer";
    private static final String BYTECODE_FILE_EXTENSION = ".jbc";

    // Configuration for a single compilation run.
    //
    // - inputFilePath
    // - outputFilePath
    // - loggingEnabled
    // - dumpLexer
    // - dumpAst
    // - dumpSymbolTable

    private final String inputFilePath;
    private final String outputFilePath;
    private final boolean loggingEnabled;
    private final boolean dumpLexer;
    private final boolean dumpAst;
    private final boolean dumpSymbolTable;

    private CompilerOptions(
            String inputFilePath,
            String outputFilePath,
            boolean loggingEnabled,
            boolean dumpLexer,
            boolean dumpAst,
            boolean dumpSymbolTable) {

        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.loggingEnabled = loggingEnabled;
        this.dumpLexer = dumpLexer;
        this.dumpAst = dumpAst;
        this.dumpSymbolTable = dumpSymbolTable;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public boolean isDumpLexer() {
        return dumpLexer;
    }

    public boolean isDumpAst() {
        return dumpAst;
    }

    public boolean isDumpSymbolTable() {
        return dumpSymbolTable;
    }

    public static CompilerOptions create(String... args) {
        String inputFilePath = null;
        String outputFilePath = null;
        boolean loggingEnabled = false;
        boolean dumpLexer = false;
        boolean dumpAst = false;
        boolean dumpSymbolTable = false;

        if (args.length == 2 && !args[0].startsWith("-") && !args[1].startsWith("-")) {
            inputFilePath = args[0];
            outputFilePath = args[1];
        } else {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--in-file", "-i" -> inputFilePath = readRequiredValue(args, ++i, arg);
                    case "--out-file", "-o" -> outputFilePath = readRequiredValue(args, ++i, arg);
                    case "--dump-lexer" -> dumpLexer = true;
                    case "--dump-ast" -> dumpAst = true;
                    case "--dump-symboltable" -> dumpSymbolTable = true;
                    case "--logging" -> loggingEnabled = true;
                    default -> throw new IllegalArgumentException("Unknown compiler option: " + arg);
                }
            }
        }

        if (inputFilePath == null || outputFilePath == null) {
            throw new IllegalArgumentException(
                    "Usage: compiler --in-file <source.javer> --out-file <output-path-without-extension> " +
                            "[--dump-lexer] [--dump-ast] [--dump-symboltable] [--logging]");
        }

        inputFilePath = validateInputFilePath(inputFilePath);
        outputFilePath = normalizeOutputFilePath(outputFilePath);

        return new CompilerOptions(
                inputFilePath,
                outputFilePath,
                loggingEnabled,
                dumpLexer,
                dumpAst,
                dumpSymbolTable
        );
    }

    private static String readRequiredValue(String[] args, int index, String optionName) {
        if (index >= args.length || args[index].startsWith("-")) {
            throw new IllegalArgumentException("Missing value for compiler option: " + optionName);
        }
        return args[index];
    }

    private static String validateInputFilePath(String inputFilePath) {
        String fileName = fileName(inputFilePath);
        if (!fileName.endsWith(SOURCE_FILE_EXTENSION)) {
            throw new IllegalArgumentException(
                    "Input file must have extension " + SOURCE_FILE_EXTENSION + ": " + inputFilePath);
        }
        return inputFilePath;
    }

    private static String normalizeOutputFilePath(String outputFilePath) {
        String fileName = fileName(outputFilePath);
        if (fileName.endsWith(BYTECODE_FILE_EXTENSION)) {
            throw new IllegalArgumentException(
                    "Output path must be provided without " + BYTECODE_FILE_EXTENSION
                            + " extension: " + outputFilePath);
        }
        if (fileName.contains(".")) {
            throw new IllegalArgumentException(
                    "Output path must be provided without file extension: " + outputFilePath);
        }
        return outputFilePath + BYTECODE_FILE_EXTENSION;
    }

    private static String fileName(String path) {
        Path fileName = Path.of(path).getFileName();
        return fileName == null ? path : fileName.toString();
    }
}
