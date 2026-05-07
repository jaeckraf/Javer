package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, SymbolTableEntry> entries = new HashMap<>();
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Adds the entry to this scope. Returns {@code false} if an entry with the same
     * name already exists in this scope (the existing entry is kept); the caller
     * is responsible for reporting the duplicate diagnostic.
     */
    public boolean addEntry(SymbolTableEntry entry) {
        if (entries.containsKey(entry.getName())) {
            return false;
        }
        entries.put(entry.getName(), entry);
        return true;
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