package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

@JacocoGenerated("Skeleton only, remove when fully implemented")
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
        diagnostics.add(diagnostic);
        // TODO: Implement error limit check (throw exception if exceeded)
    }

    public void add(SourceLocation location, Severity severity, String message) {

    }

    /**
     * Checks if there are any diagnostics with ERROR or SEVERE severity in the bag.
     *
     * @return true if there are errors, false otherwise.
     */
    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.getSeverity() == Severity.ERROR || d.getSeverity() == Severity.SEVERE);
    }

    /**
     * Generates a formatted, human-readable report of all diagnostics in the current phase.
     * Uses the SourceCache to print the exact line of code where the error occurred.
     *
     * @return The formatted report string.
     */
    public String dumpReport() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Error Report ===\n");
        sb.append("File: ").append(filePath).append("\n");
        sb.append("Phase: ").append(phase).append("\n\n");

        if (diagnostics.isEmpty()) {
            sb.append("No diagnostics.\n");
            return sb.toString();
        }

        for (Diagnostic d : diagnostics) {
            sb.append(d.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Clears all diagnostics from the bag, preparing it for the next compiler phase.
     */
    public void flush() {
        // TODO: Implement
    }
}
