package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.HashMap;
import java.util.Map;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Diagnostic;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class SymbolTable {
    private final Map<String, SymbolTableEntry> entries = new HashMap<>();
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void addEntry(SymbolTableEntry entry, DiagnosticBag diagnosticBag) {
        if (entries.containsKey(entry.getName())) {
            Diagnostic diagnostic = new Diagnostic(
                    new SourceLocation(1,2,1), // TODO: Provide actual source location
                    Severity.ERROR,
                    "Duplicate symbol: " + entry.getName());
            diagnosticBag.add(diagnostic);
        }
        entries.put(entry.getName(), entry);
    }

    public SymbolTableEntry getEntry(String name) {
        SymbolTableEntry entry = entries.get(name);
        if (entry == null && parent != null) {
            entry = parent.getEntry(name);
        }
        return entry;
    }

    public boolean contains(String name) {
        return entries.containsKey(name) || (parent != null && parent.contains(name));
    }

    public Map<String, SymbolTableEntry> getAllEntries() {
        return new HashMap<>(entries);
    }
}