package ch.zhaw.it.pm4.javer.compiler.parser;

import java.util.Arrays;
import java.util.Iterator;

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

        Iterator<String> argIterator = Arrays.asList(args).iterator();

        while (argIterator.hasNext()) {
            String arg = argIterator.next();

            switch (arg) {
                case "-i", "--input"  -> inputFile = extractValue(argIterator, arg);
                case "-o", "--output" -> outputFile = extractValue(argIterator, arg);
                default -> {
                    if (arg.startsWith("-")) {
                        throw new IllegalArgumentException("Unknown compiler option: '" + arg + "'");
                    }
                    throw new IllegalArgumentException(
                            "Unexpected argument: '" + arg + "'. Please use flags, e.g., '-i " + arg + "'"
                    );
                }
            }
        }

        if (inputFile == null) {
            throw new IllegalArgumentException("An input file (-i or --input) is required.");
        }

        if (outputFile == null) {
            int dotIndex = inputFile.lastIndexOf('.');
            if (dotIndex != -1) {
                outputFile = inputFile.substring(0, dotIndex) + ".jbc";
            } else {
                outputFile = inputFile + ".jbc";
            }
        }

        return new CompilerArguments(inputFile, outputFile);
    }

    private String extractValue(Iterator<String> iterator, String currentFlag) {
        if (iterator.hasNext()) {
            String nextValue = iterator.next();
            if (!nextValue.startsWith("-")) {
                return nextValue;
            }
        }
        throw new IllegalArgumentException("Missing value for option: '" + currentFlag + "'");
    }
}
