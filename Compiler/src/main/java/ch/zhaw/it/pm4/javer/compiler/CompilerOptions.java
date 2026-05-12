package ch.zhaw.it.pm4.javer.compiler;

public class CompilerOptions {

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
                    "Usage: compiler --in-file <path> --out-file <path> " +
                            "[--dump-lexer] [--dump-ast] [--dump-symboltable] [--logging]");
        }

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
}
