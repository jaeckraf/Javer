package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.misc.JaverLogger;

import java.nio.file.Path;

/**
 * Validated command-line configuration for one compiler run.
 */
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

    /**
     * Parses and validates compiler command-line arguments.
     *
     * @param args command-line options
     * @return validated compiler options
     * @throws IllegalArgumentException if an option is unknown, missing, or has
     *                                  an invalid path value
     */
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
            int i = 0;
            while (i < args.length) {
                String arg = args[i];
                switch (arg) {
                    case "--in-file", "-i" -> {
                        i++;
                        inputFilePath = readRequiredValue(args, i, arg);
                    }
                    case "--out-file", "-o" -> {
                        i++;
                        outputFilePath = readRequiredValue(args, i, arg);
                    }
                    case "--dump-lexer" -> dumpLexer = true;
                    case "--dump-ast" -> dumpAst = true;
                    case "--dump-symboltable" -> dumpSymbolTable = true;
                    case "--logging" -> loggingEnabled = true;
                    default -> {
                        JaverLogger.error("Unknown compiler option: " + arg);
                        throw new IllegalArgumentException("Unknown compiler option: " + arg);
                    }
                }
                i++;
            }
        }

        if (inputFilePath == null || outputFilePath == null) {
            JaverLogger.error("missing input or output files");
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

    /**
     * @return source file path with a {@code .javer} extension
     */
    public String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * @return normalized bytecode output file path with a {@code .jbc}
     * extension
     */
    public String getOutputFilePath() {
        return outputFilePath;
    }

    /**
     * @return true when compiler-internal logging is enabled
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * @return true when token dump output should be printed
     */
    public boolean isDumpLexer() {
        return dumpLexer;
    }

    /**
     * @return true when AST dump output should be printed
     */
    public boolean isDumpAst() {
        return dumpAst;
    }

    /**
     * @return true when symbol-table dump output should be printed
     */
    public boolean isDumpSymbolTable() {
        return dumpSymbolTable;
    }
}
