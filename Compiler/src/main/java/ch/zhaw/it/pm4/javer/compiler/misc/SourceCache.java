package ch.zhaw.it.pm4.javer.compiler.misc;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.misc.JaverLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Holds the full source code of the single input file and maintains
 * an internal cache of the lines for fast O(1) lookups during error reporting.
 */
@JacocoGenerated("Skeleton only, remove when fully implemented")
public class SourceCache {

    private final String filePath;
    private final String sourceCode;
    private final List<String> cachedLines;

    /**
     * Initializes the cache by reading the source code from the given file path
     * and building the internal line lookup table.
     *
     * @param filePath The path to the source file to be read.
     */
    public SourceCache(String filePath) {
        this.filePath = filePath;
        try {
            this.sourceCode = Files.readString(Path.of(filePath));
            this.cachedLines = buildCache(this.sourceCode);
        } catch (IOException e) {
            JaverLogger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        // Read the entire file into a single string.
        // Path.of() and Files.readString() are standard in modern Java.

    }

    /**
     * Internal helper to pre-compute the lines of the source code.
     *
     * @param source The raw source string.
     * @return A list of individual lines.
     */
    private List<String> buildCache(String source) {
        // TODO: Implement splitting the source string by newlines
        // Hint: In modern Java, you can use source.lines().toList();
        return null;
    }

    /**
     * Retrieves the file path this cache was built from.
     * Useful for the DiagnosticBag when printing error reports.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Retrieves the entire raw source code.
     * Primarily used by the Lexer to begin tokenization.
     *
     * @return The full source code string.
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Retrieves a specific line of code based on its line number (1-indexed)
     * using the pre-built cache.
     *
     * @param lineNumber The 1-indexed line number to fetch.
     * @return The string content of that specific line, or a fallback message if out of bounds.
     */
    public String getLine(int lineNumber) {
        // TODO: Implement fetching from cachedLines.
        // Remember to adjust for 1-indexed line numbers (e.g., lineNumber - 1)
        // and handle index out of bounds safely!
        return "";
    }
}
