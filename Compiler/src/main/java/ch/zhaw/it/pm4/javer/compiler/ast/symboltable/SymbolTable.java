package ch.zhaw.it.pm4.javer.compiler.ast.symboltable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private final Map<String, FunctionSymbolTableEntry> functions = new HashMap<>();
    private final Map<String, StructSymbolTableEntry> structs = new HashMap<>();
    private final Map<String, EnumSymbolTableEntry> enums = new HashMap<>();
    private final Map<String, ParameterSymbolTableEntry> parameters = new HashMap<>();
    private final Map<String, VariableSymbolTableEntry> locals = new HashMap<>();
    private final Map<String, FieldSymbolTableEntry> fields = new HashMap<>();
    private final Map<String, EnumValueSymbolTableEntry> enumValues = new HashMap<>();
    private final SymbolTable parent;
    private final List<SymbolTable> children = new ArrayList<>();

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public boolean defineFunction(FunctionSymbolTableEntry entry) {
        return putIfAbsent(functions, entry);
    }

    public boolean defineStruct(StructSymbolTableEntry entry) {
        return putIfAbsent(structs, entry);
    }

    public boolean defineEnum(EnumSymbolTableEntry entry) {
        return putIfAbsent(enums, entry);
    }

    public boolean defineParameter(ParameterSymbolTableEntry entry) {
        if (resolveVariable(entry.getName()) != null) {
            return false;
        }
        parameters.put(entry.getName(), entry);
        return true;
    }

    public boolean defineLocal(VariableSymbolTableEntry entry) {
        if (resolveVariable(entry.getName()) != null) {
            return false;
        }
        locals.put(entry.getName(), entry);
        return true;
    }

    public boolean defineField(FieldSymbolTableEntry entry) {
        return putIfAbsent(fields, entry);
    }

    public boolean defineEnumValue(EnumValueSymbolTableEntry entry) {
        return putIfAbsent(enumValues, entry);
    }

    private static <T extends SymbolTableEntry> boolean putIfAbsent(Map<String, T> entries, T entry) {
        if (entries.containsKey(entry.getName())) {
            return false;
        }
        entries.put(entry.getName(), entry);
        return true;
    }

    public boolean addEntry(SymbolTableEntry entry) {
        return switch (entry.getKind()) {
            case FUNCTION -> defineFunction((FunctionSymbolTableEntry) entry);
            case STRUCT -> defineStruct((StructSymbolTableEntry) entry);
            case ENUM -> defineEnum((EnumSymbolTableEntry) entry);
            case ENUM_VALUE -> defineEnumValue((EnumValueSymbolTableEntry) entry);
            case PARAMETER -> defineParameter((ParameterSymbolTableEntry) entry);
            case VARIABLE -> defineLocal((VariableSymbolTableEntry) entry);
            case FIELD -> defineField((FieldSymbolTableEntry) entry);
        };
    }

    public SymbolTableEntry getEntry(String name) {
        SymbolTableEntry entry = resolveVariable(name);
        if (entry != null) {
            return entry;
        }
        entry = resolveFunction(name);
        if (entry != null) {
            return entry;
        }
        entry = resolveStruct(name);
        if (entry != null) {
            return entry;
        }
        return resolveEnum(name);
    }

    public FunctionSymbolTableEntry resolveFunction(String name) {
        FunctionSymbolTableEntry entry = functions.get(name);
        return entry != null || parent == null ? entry : parent.resolveFunction(name);
    }

    public StructSymbolTableEntry resolveStruct(String name) {
        StructSymbolTableEntry entry = structs.get(name);
        return entry != null || parent == null ? entry : parent.resolveStruct(name);
    }

    public EnumSymbolTableEntry resolveEnum(String name) {
        EnumSymbolTableEntry entry = enums.get(name);
        return entry != null || parent == null ? entry : parent.resolveEnum(name);
    }

    public StorageSymbolTableEntry resolveVariable(String name) {
        VariableSymbolTableEntry local = locals.get(name);
        if (local != null) {
            return local;
        }
        ParameterSymbolTableEntry parameter = parameters.get(name);
        if (parameter != null) {
            return parameter;
        }
        return parent == null ? null : parent.resolveVariable(name);
    }

    public FieldSymbolTableEntry resolveField(String name) {
        return fields.get(name);
    }

    public EnumValueSymbolTableEntry resolveEnumValue(String name) {
        return enumValues.get(name);
    }

    public EnumValueSymbolTableEntry resolveUniqueEnumValue(String name) {
        EnumValueSymbolTableEntry found = null;
        for (EnumSymbolTableEntry enumEntry : getRoot().enums.values()) {
            if (enumEntry.getSymbolTable() == null) {
                continue;
            }
            EnumValueSymbolTableEntry candidate = enumEntry.getSymbolTable().resolveEnumValue(name);
            if (candidate == null) {
                continue;
            }
            if (found != null) {
                return null;
            }
            found = candidate;
        }
        return found;
    }

    public boolean isAmbiguousEnumValue(String name) {
        boolean found = false;
        for (EnumSymbolTableEntry enumEntry : getRoot().enums.values()) {
            if (enumEntry.getSymbolTable() == null) {
                continue;
            }
            if (enumEntry.getSymbolTable().resolveEnumValue(name) == null) {
                continue;
            }
            if (found) {
                return true;
            }
            found = true;
        }
        return false;
    }

    private SymbolTable getRoot() {
        SymbolTable scope = this;
        while (scope.parent != null) {
            scope = scope.parent;
        }
        return scope;
    }

    public void addChild(SymbolTable scope) {
        children.add(scope);
    }

    public boolean contains(String name) {
        return getEntry(name) != null;
    }

    public Map<String, SymbolTableEntry> getAllEntries() {
        Map<String, SymbolTableEntry> entries = new LinkedHashMap<>();
        functions.forEach((name, entry) -> entries.put("function " + name, entry));
        structs.forEach((name, entry) -> entries.put("struct " + name, entry));
        enums.forEach((name, entry) -> entries.put("enum " + name, entry));
        parameters.forEach((name, entry) -> entries.put("parameter " + name, entry));
        locals.forEach((name, entry) -> entries.put("local " + name, entry));
        fields.forEach((name, entry) -> entries.put("field " + name, entry));
        enumValues.forEach((name, entry) -> entries.put("enum-value " + name, entry));
        return entries;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public List<SymbolTable> getChildren() {
        return children;
    }
}
