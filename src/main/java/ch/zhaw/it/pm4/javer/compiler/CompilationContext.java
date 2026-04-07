package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.JaverLogger;
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

    private static CompilationContext instance;

    private CompilerOptions options;
    private DiagnosticBag diagnostics;
    private SourceCache sourceCache;

    private CompilationContext(
            CompilerOptions options,
            DiagnosticBag diagnostics,
            SourceCache sourceCache) {

        this.options = options;
        this.diagnostics = diagnostics;
        this.sourceCache = sourceCache;
    }

    /**
     * Returns the global context instance.
     */
    public static CompilationContext getInstance() {
        if (instance == null) {
            instance = new CompilationContext(null, null, null);
        }
        return instance;
    }

    public CompilerOptions getOptions() {
        return options;
    }

    public void setOptions(CompilerOptions options) {
        this.options = options;
    }

    public DiagnosticBag getDiagnostics() {
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