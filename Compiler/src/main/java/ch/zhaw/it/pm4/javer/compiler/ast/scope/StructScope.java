package ch.zhaw.it.pm4.javer.compiler.ast.scope;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FieldEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.SymbolEntry;

public final class StructScope {

    private final Map<String, FieldEntry> fields = new LinkedHashMap<>();
    private int sizeBytes;

    public boolean defineField(FieldEntry entry) {
        if (fields.containsKey(entry.getName())) {
            return false;
        }
        fields.put(entry.getName(), entry);
        sizeBytes += entry.getSizeBytes();
        return true;
    }

    public FieldEntry resolveField(String name) {
        return fields.get(name);
    }

    public Map<String, FieldEntry> getFields() {
        return fields;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Map<String, SymbolEntry> getAllEntries() {
        Map<String, SymbolEntry> entries = new LinkedHashMap<>();
        fields.forEach((name, entry) -> entries.put("field " + name, entry));
        return entries;
    }
}
