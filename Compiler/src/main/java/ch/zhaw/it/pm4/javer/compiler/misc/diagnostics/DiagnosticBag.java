package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.CompilationPhase;
import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public class DiagnosticBag {

    private static final int TAB_WIDTH = 4;

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

    public void setPhase(CompilationPhase phase) {
        this.phase = phase;
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
        diagnostics.add(new Diagnostic(location, severity, message));
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

        for (int i = 0; i < diagnostics.size(); i++) {
            sb.append(formatDiagnostic(diagnostics.get(i)));
            if (i + 1 < diagnostics.size()) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private String formatDiagnostic(Diagnostic diagnostic) {
        SourceLocation location = diagnostic.getLocation();
        StringBuilder sb = new StringBuilder();

        sb.append("[")
                .append(diagnostic.getSeverity().name())
                .append("] ")
                .append(diagnostic.getMessage())
                .append("\n");

        if (location == null) {
            return sb.toString();
        }

        sb.append("  --> ")
                .append(filePath)
                .append(":")
                .append(location.lineNumber())
                .append(":")
                .append(location.startColumn())
                .append("\n");

        if (sourceCache != null) {
            appendSourceExcerpt(sb, location);
        }

        return sb.toString();
    }

    private void appendSourceExcerpt(StringBuilder sb, SourceLocation location) {
        String rawLine = sourceCache.getLine(location.lineNumber());
        String line = expandTabs(rawLine);
        String lineNumber = Integer.toString(location.lineNumber());
        String gutterPadding = " ".repeat(lineNumber.length());

        sb.append(gutterPadding).append(" |").append("\n");
        sb.append(lineNumber).append(" | ").append(line).append("\n");
        sb.append(gutterPadding).append(" | ").append(markerFor(location)).append("\n");
    }

    private String markerFor(SourceLocation location) {
        int start = Math.max(1, location.startColumn());
        int end = Math.max(start, location.endColumn());
        return " ".repeat(start - 1) + "^".repeat(end - start + 1);
    }

    private String expandTabs(String line) {
        StringBuilder expanded = new StringBuilder();
        int column = 1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\t') {
                int spaces = TAB_WIDTH - ((column - 1) % TAB_WIDTH);
                expanded.append(" ".repeat(spaces));
                column += spaces;
            } else {
                expanded.append(c);
                column++;
            }
        }
        return expanded.toString();
    }

    /**
     * Clears all diagnostics from the bag, preparing it for the next compiler phase.
     */
    public void flush() {
        // TODO: Implement
    }
}
