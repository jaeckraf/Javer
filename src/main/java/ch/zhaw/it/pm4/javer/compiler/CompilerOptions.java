package ch.zhaw.it.pm4.javer.compiler;

public class CompilerOptions {

    // Configuration for a single compilation run.
    //
    // - inputFilePath
    // - outputFilePath
    // - loggingEnabled
    // - dumpAst
    // - optimizationLevel

    private final String inputFilePath;
    private final String outputFilePath;
    private final boolean loggingEnabled;
    private final boolean dumpAst;
    private final int optimizationLevel;

    private CompilerOptions(
            String inputFilePath,
            String outputFilePath,
            boolean loggingEnabled,
            boolean dumpAst,
            int optimizationLevel) {

        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.loggingEnabled = loggingEnabled;
        this.dumpAst = dumpAst;
        this.optimizationLevel = optimizationLevel;
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

    public boolean isDumpAst() {
        return dumpAst;
    }

    public int getOptimizationLevel() {
        return optimizationLevel;
    }

    public static CompilerOptions create(String...args) {

        String inputFilePath = args[0];
        String outputFilePath = args[1];
        return new CompilerOptions(
                inputFilePath,
                outputFilePath,
                true,
                false,
                0
        );
    }
}