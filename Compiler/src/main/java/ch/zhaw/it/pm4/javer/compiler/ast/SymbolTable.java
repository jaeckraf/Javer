package ch.zhaw.it.pm4.javer.compiler.ast;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, SymbolTableEntry> entries = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public boolean addEntry(SymbolTableEntry entry) {
        if (entries.containsKey(entry.getName()) && !entry.getName().equals("block"))
            return false;
        else if (entries.containsKey(entry.getName()) && !entry.getName().equals("block") && entry instanceof BlockSymbolTableEntry blockEntry) {
            int i = 1;
            String newName = "block" + "_" + i;
            while (entries.containsKey(newName)) {
                i++;
                newName = "block" + "_" + i;
            }
            blockEntry.setName(newName);
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

    public SymbolTable getParent() {
        return parent;
    }
}