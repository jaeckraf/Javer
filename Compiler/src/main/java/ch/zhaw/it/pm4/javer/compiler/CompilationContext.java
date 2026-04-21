package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

public class CompilationContext {

    /**
     * Shared infrastructure for all phases.
     *
     * Avoids passing multiple dependencies explicitly.
     *
     * - CompilerOptions
     * - Logger
     * - DiagnosticBag
     * - SourceCache
     */


    private CompilerOptions options;
    private DiagnosticBag diagnostics;
    private SourceCache sourceCache;

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