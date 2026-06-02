package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

/**
 * Mutable context shared by compiler phases during one compilation run.
 */
public class CompilationContext {

    private CompilerOptions options;
    private DiagnosticBag diagnostics;
    private SourceCache sourceCache;

    /**
     * Creates a compilation context from the phase-shared objects.
     *
     * @param options     compiler options for this run
     * @param diagnostics diagnostic collector for this run
     * @param sourceCache cached source text and source file metadata
     */
    public CompilationContext(
            CompilerOptions options,
            DiagnosticBag diagnostics,
            SourceCache sourceCache) {

        this.options = options;
        this.diagnostics = diagnostics;
        this.sourceCache = sourceCache;
    }


    public CompilerOptions getOptions() {
        return options;
    }

    public void setOptions(CompilerOptions options) {
        this.options = options;
    }

    public DiagnosticBag getDiagnosticBag() {
        return diagnostics;
    }

    public void setDiagnostics(DiagnosticBag diagnostics) {
        this.diagnostics = diagnostics;
    }

    public SourceCache getSourceCache() {
        return sourceCache;
    }

    public void setSourceCache(SourceCache sourceCache) {
        this.sourceCache = sourceCache;
    }
}
