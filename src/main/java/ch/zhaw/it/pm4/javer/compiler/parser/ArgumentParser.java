package ch.zhaw.it.pm4.javer.compiler.parser;

/**
 * Parses raw command-line arguments into a structured {@link CompilerArguments} object.
 */
public class ArgumentParser {

    /**
     * Parses the given array of arguments.
     * <p>
     * Current Constraint: Only file paths are allowed. If the parser detects
     * a flag (an argument starting with '-'), it will reject it.
     *
     * @param args The command-line arguments passed from the main method.
     * @return A structured {@link CompilerArguments} object.
     * @throws IllegalArgumentException if an invalid flag or too many arguments are provided.
     */
    public CompilerArguments parse(String[] args) throws IllegalArgumentException {
        String inputFile = null;
        String outputFile = null;

        if (args == null || args.length == 0) {
            return new CompilerArguments(null, null);
        }

        for (String arg : args) {
            if (arg.startsWith("-")) {
                throw new IllegalArgumentException(
                        "Unknown compiler option: '" + arg + "'. Only file paths are allowed at the moment."
                );
            }

            if (inputFile == null) {
                inputFile = arg;
            } else if (outputFile == null) {
                outputFile = arg;
            } else {
                throw new IllegalArgumentException(
                        "Too many arguments provided. Expected at most an input file and an output file."
                );
            }
        }

        return new CompilerArguments(inputFile, outputFile);
    }
}
