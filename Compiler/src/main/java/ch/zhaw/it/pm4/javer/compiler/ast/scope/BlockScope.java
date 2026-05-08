package ch.zhaw.it.pm4.javer.compiler.ast.scope;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.SymbolEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.VariableEntry;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;

public final class BlockScope {

    private final BlockScope parent;
    private final FunctionScope functionScope;
    private final Map<String, VariableEntry> variables = new LinkedHashMap<>();
    private final List<BlockScope> children = new ArrayList<>();

    public BlockScope(BlockScope parent, FunctionScope functionScope) {
        this.parent = parent;
        this.functionScope = functionScope;
    }

    public boolean canDefine(String name) {
        return functionScope.resolveParameter(name) == null
                && resolveVisibleVariable(name) == null;
    }

    public boolean defineVariable(VariableEntry entry) {
        if (!canDefine(entry.getName())) {
            return false;
        }
        variables.put(entry.getName(), entry);
        return true;
    }

    public VariableEntry resolveVisibleVariable(String name) {
        VariableEntry variable = variables.get(name);
        if (variable != null) {
            return variable;
        }
        return parent == null ? null : parent.resolveVisibleVariable(name);
    }

    public VariableEntry resolveVisibleVariable(String name, SourceLocation useLocation) {
        VariableEntry variable = variables.get(name);
        if (variable != null && isVisibleAt(variable, useLocation)) {
            return variable;
        }
        return parent == null ? null : parent.resolveVisibleVariable(name, useLocation);
    }

    private boolean isVisibleAt(VariableEntry variable, SourceLocation useLocation) {
        SourceLocation visibilityStart = variable.getVisibilityStart();
        if (visibilityStart == null || useLocation == null) {
            return true;
        }
        if (visibilityStart.lineNumber() < useLocation.lineNumber()) {
            return true;
        }
        return visibilityStart.lineNumber() == useLocation.lineNumber()
                && visibilityStart.endColumn() <= useLocation.startColumn();
    }

    public void addChild(BlockScope scope) {
        children.add(scope);
    }

    public BlockScope getParent() {
        return parent;
    }

    public FunctionScope getFunctionScope() {
        return functionScope;
    }

    public Map<String, VariableEntry> getVariables() {
        return variables;
    }

    public List<BlockScope> getChildren() {
        return children;
    }

    public Map<String, SymbolEntry> getAllEntries() {
        Map<String, SymbolEntry> entries = new LinkedHashMap<>();
        variables.forEach((name, entry) -> entries.put("local " + name, entry));
        return entries;
    }
}
