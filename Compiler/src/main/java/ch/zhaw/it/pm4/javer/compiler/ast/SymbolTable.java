package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, SymbolTableEntry> entries = new HashMap<>();

    public void addEntry(SymbolTableEntry entry) {
        entries.put(entry.getName(), entry);
    }

    public SymbolTableEntry getEntry(String name) {
        return entries.get(name);
    }

    public boolean contains(String name) {
        return entries.containsKey(name);
    }

    public Map<String, SymbolTableEntry> getAllEntries() {
        return new HashMap<>(entries);
    }
}