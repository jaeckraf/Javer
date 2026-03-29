package ch.zhaw.it.pm4.javer.compiler.parser;

/**
 * Parses raw command-line arguments into a structured {@link CompilerArguments} object.
 */
public class ArgumentParser {

    /**
     * Parses the given array of arguments using a scalable flag-based approach.
     * <p>
     * Expected format: -i <inputFile> [-o <outputFile>]
     *
     * @param args The command-line arguments passed from the main method.
     * @return A structured {@link CompilerArguments} object.
     * @throws IllegalArgumentException if an invalid flag is provided or a value is missing.
     */
    public CompilerArguments parse(String[] args) throws IllegalArgumentException {
        String inputFile = null;
        String outputFile = null;

        if (args == null || args.length == 0) {
            return new CompilerArguments(null, null);
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-i":
                case "--input":
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        inputFile = args[++i];
                    } else {
                        throw new IllegalArgumentException("Missing value for input option: '" + arg + "'");
                    }
                    break;

                case "-o":
                case "--output":
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        outputFile = args[++i];
                    } else {
                        throw new IllegalArgumentException("Missing value for output option: '" + arg + "'");
                    }
                    break;

                default:
                    if (arg.startsWith("-")) {
                        throw new IllegalArgumentException("Unknown compiler option: '" + arg + "'");
                    } else {
                        throw new IllegalArgumentException(
                                "Unexpected argument: '" + arg + "'. Please use flags, e.g., '-i " + arg + "'"
                        );
                    }
            }
        }

        return new CompilerArguments(inputFile, outputFile);
    }
}
