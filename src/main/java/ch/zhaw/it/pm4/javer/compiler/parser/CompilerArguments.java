package ch.zhaw.it.pm4.javer.compiler.parser;

public record CompilerArguments(String inputFile, String outputFile) {

    public boolean hasInputFile() {
        return inputFile != null && !inputFile.trim().isEmpty();
    }
}
