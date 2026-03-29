package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.diagnostics.DiagnosticBag;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Parses raw command-line arguments into a structured {@link CompilerArguments} object.
 * Errors encountered during parsing are reported to the provided {@link DiagnosticBag}.
 */
public class ArgumentParser {

    private final DiagnosticBag diagnostics;

    /**
     * Creates a new ArgumentParser.
     *
     * @param diagnostics The DiagnosticBag where argument errors will be reported.
     */
    public ArgumentParser(DiagnosticBag diagnostics) {
        this.diagnostics = diagnostics;
    }

    /**
     * Parses the given array of arguments using a scalable flag-based approach.
     * <p>
     * Expected format: -i <inputFile> [-o <outputFile>]
     *
     * @param args The command-line arguments passed from the main method.
     * @return A structured {@link CompilerArguments} object.
     */
    public CompilerArguments parse(String[] args) {
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
                        diagnostics.reportError("Unknown compiler option: '" + arg + "'");
                    } else {
                        diagnostics.reportError("Unexpected argument: '" + arg + "'. Please use flags, e.g., '-i " + arg + "'");
                    }
                }
            }
        }

        if (inputFile == null && !diagnostics.hasErrors()) {
            diagnostics.reportError("An input file (-i or --input) is required.");
        }

        if (outputFile == null && inputFile != null) {
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

        diagnostics.reportError("Missing value for option: '" + currentFlag + "'");
        return null;
    }
}
