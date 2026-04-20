package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
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
     * - SymbolTable
     */


    private CompilerOptions options;
    private DiagnosticBag diagnostics;
    private SourceCache sourceCache;
    private SymbolTable symbolTable;

    public CompilationContext(
            CompilerOptions options,
            DiagnosticBag diagnostics,
            SourceCache sourceCache) {

        this.options = options;
        this.diagnostics = diagnostics;
        this.sourceCache = sourceCache;
        this.symbolTable = new SymbolTable();
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

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}