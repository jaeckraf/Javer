package ch.zhaw.it.pm4.javer.compiler.ast.scope;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.LabelEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.ParameterEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.SymbolEntry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Symbol scope for parameters, labels, and the root block of a function.
 */
public final class FunctionScope {

    private final FunctionEntry owner;
    private final Map<String, ParameterEntry> parameters = new LinkedHashMap<>();
    private final Map<String, LabelEntry> labels = new LinkedHashMap<>();
    private BlockScope rootBlock;

    public FunctionScope(FunctionEntry owner) {
        this.owner = owner;
    }

    public FunctionEntry getOwner() {
        return owner;
    }

    public boolean defineParameter(ParameterEntry entry) {
        if (parameters.containsKey(entry.getName())) {
            return false;
        }
        parameters.put(entry.getName(), entry);
        return true;
    }

    public ParameterEntry resolveParameter(String name) {
        return parameters.get(name);
    }

    public boolean defineLabel(LabelEntry entry) {
        if (labels.containsKey(entry.getName())) {
            return false;
        }
        labels.put(entry.getName(), entry);
        return true;
    }

    public LabelEntry resolveLabel(String name) {
        return labels.get(name);
    }

    public BlockScope getRootBlock() {
        return rootBlock;
    }

    public void setRootBlock(BlockScope rootBlock) {
        this.rootBlock = rootBlock;
    }

    public Map<String, ParameterEntry> getParameters() {
        return parameters;
    }

    public Map<String, LabelEntry> getLabels() {
        return labels;
    }

    public Map<String, SymbolEntry> getAllEntries() {
        Map<String, SymbolEntry> entries = new LinkedHashMap<>();
        parameters.forEach((name, entry) -> entries.put("parameter " + name, entry));
        labels.forEach((name, entry) -> entries.put("label " + name, entry));
        return entries;
    }
}
