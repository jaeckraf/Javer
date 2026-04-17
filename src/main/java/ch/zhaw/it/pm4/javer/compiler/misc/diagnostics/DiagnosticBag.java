package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticBag {

    private final int errorLimit;
    private final String filePath;
    private final SourceCache sourceCache;

    private CompilationPhase phase;
    private final List<Diagnostic> diagnostics;

    /**
     * Initializes a new DiagnosticBag.
     *
     * @param filePath   The path of the file being compiled.
     * @param errorLimit The maximum number of errors before compilation aborts.
     *                   // @param sourceCache The cache holding the raw source code text.
     */
    public DiagnosticBag(String filePath, int errorLimit, CompilationPhase compilationPhase, SourceCache sourceCache) {
        this.filePath = filePath;
        this.errorLimit = errorLimit;
        this.sourceCache = sourceCache;
        this.diagnostics = new ArrayList<>();
        this.phase = compilationPhase;
    }

    /**
     * Sets the current compiler phase (e.g., "Lexer", "Parser").
     *
     * @param phase The name of the phase.
     */
    public void setPhase(String phase) {
        // TODO: Implement
    }

    /**
     * Adds a newly created diagnostic to the bag and enforces the error limit.
     *
     * @param diagnostic The diagnostic to add.
     */
    public void add(Diagnostic diagnostic) {
        // TODO: Implement adding to list
        // TODO: Implement error limit check (throw exception if exceeded)
    }

    /**
     * Checks if there are any diagnostics with ERROR or SEVERE severity in the bag.
     *
     * @return true if there are errors, false otherwise.
     */
    public boolean hasErrors() {
        // TODO: Implement
        return false;
    }

    /**
     * Generates a formatted, human-readable report of all diagnostics in the current phase.
     * Uses the SourceCache to print the exact line of code where the error occurred.
     *
     * @return The formatted report string.
     */
    public String dumpReport() {
        // TODO: Implement
        return "Error Report:\n";
    }

    /**
     * Clears all diagnostics from the bag, preparing it for the next compiler phase.
     */
    public void flush() {
        // TODO: Implement
    }
}
